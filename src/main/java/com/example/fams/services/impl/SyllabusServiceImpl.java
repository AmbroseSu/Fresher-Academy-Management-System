package com.example.fams.services.impl;

import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.SyllabusDTO;
import com.example.fams.dto.request.DeleteReplace;
import com.example.fams.entities.*;
import com.example.fams.entities.enums.DeliveryType;
import com.example.fams.repository.*;
import com.example.fams.services.ISyllabusService;
import com.example.fams.services.ServiceUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

@Service("SyllabusService")
public class SyllabusServiceImpl implements ISyllabusService {

    private final SyllabusRepository syllabusRepository;
    private final SyllabusObjectiveRepository syllabusObjectiveRepository;
    private final GenericConverter genericConverter;
    private final TrainingProgramRepository trainingProgramRepository;
    private final LearningObjectiveRepository learningObjectiveRepository;
    private final SyllabusTrainingProgramRepository syllabusTrainingProgramRepository;
    private final MaterialRepository materialRepository;
    private final SyllabusMaterialRepository syllabusMaterialRepository;
    private final UnitRepository unitRepository;
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");

    public SyllabusServiceImpl(SyllabusRepository syllabusRepository, SyllabusObjectiveRepository syllabusObjectiveRepository, GenericConverter genericConverter, TrainingProgramRepository trainingProgramRepository, LearningObjectiveRepository learningObjectiveRepository, SyllabusTrainingProgramRepository syllabusTrainingProgramRepository, MaterialRepository materialRepository, SyllabusMaterialRepository syllabusMaterialRepository, UnitRepository unitRepository) {
        this.syllabusRepository = syllabusRepository;
        this.syllabusObjectiveRepository = syllabusObjectiveRepository;
        this.genericConverter = genericConverter;
        this.trainingProgramRepository = trainingProgramRepository;
        this.learningObjectiveRepository = learningObjectiveRepository;
        this.syllabusTrainingProgramRepository = syllabusTrainingProgramRepository;
        this.materialRepository = materialRepository;
        this.syllabusMaterialRepository = syllabusMaterialRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Syllabus entity = syllabusRepository.findByStatusIsTrueAndId(id);
        if (entity != null){
            SyllabusDTO result = convertSyllabusToSyllabusDTO(entity);
            return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
        }
        else{
            return ResponseUtil.error("Syllabus not found", "Cannot Find Syllabus", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Syllabus> entities = syllabusRepository.findAllByStatusIsTrue(pageable);
        List<SyllabusDTO> result = new ArrayList<>();
        convertListSyllabusToListSyllabusDTO(entities, result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                syllabusRepository.count());
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Syllabus> entities = syllabusRepository.findAll(pageable);
        List<SyllabusDTO> result = new ArrayList<>();
        convertListSyllabusToListSyllabusDTO(entities.getContent(), result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                syllabusRepository.count());
    }

    @Override
    public ResponseEntity<?> save(SyllabusDTO syllabusDTO) {
        ServiceUtils.errors.clear();
        List<Long> unitIds = syllabusDTO.getUnitIds();
        List<Long> requestLearningObjectiveIds = syllabusDTO.getLearningObjectiveIds();
        List<Long> requestTrainingProgramIds = syllabusDTO.getTrainingProgramIds();
        List<Long> requestMaterialIds = syllabusDTO.getMaterialIds();
        Syllabus entity;

        // * Validate requestDTO ( if left null, then can be updated later )
        if (unitIds != null){
            ServiceUtils.validateUnitIds(unitIds, unitRepository);
        }
        if (requestTrainingProgramIds != null){
            ServiceUtils.validateTrainingProgramIds(requestTrainingProgramIds, trainingProgramRepository);
        }
        if (requestLearningObjectiveIds != null){
            ServiceUtils.validateLearningObjectiveIds(requestLearningObjectiveIds, learningObjectiveRepository);
        }
        if (requestMaterialIds != null){
            ServiceUtils.validateMaterialIds(requestMaterialIds, materialRepository);
        }
        if (!ServiceUtils.errors.isEmpty()) {
            throw new CustomValidationException(ServiceUtils.errors);
        }

        if (syllabusDTO.getId() != null) {
            // Xử lí Update các giá trị cũ
            // Lấy Entity cũ ra
            Syllabus oldEntity = syllabusRepository.findById(syllabusDTO.getId()).get();
            // Clone cái cũ thành 1 thg entity khác
            Syllabus tempOldEntity = cloneSyllabus(oldEntity);
            // Update entity cũ bằng DTO nhập vào
//            entity = convertDtoToEntity(syllabusDTO);
            entity = (Syllabus) genericConverter.toEntity(syllabusDTO, Syllabus.class);
            // Thêm nhg attribute còn thiếu từ entity cũ vào entity mới
            entity = fillMissingAttribute(entity, tempOldEntity);

            // Xử lí quan hệ
            // Xóa quan hệ cũ trong bảng phụ
            syllabusObjectiveRepository.deleteAllBySyllabusId(syllabusDTO.getId());
            syllabusMaterialRepository.deleteAllBySyllabusId(syllabusDTO.getId());
            syllabusTrainingProgramRepository.deleteAllBySyllabusId(syllabusDTO.getId());
            // Xóa quan hệ bảng 1 nhiều
            unitRepository.findUnitsBySyllabusId(syllabusDTO.getId()).stream()
                    .peek(unit -> unit.setSyllabus(null))
                    .forEach(unitRepository::save);

            // Update quan hệ mới từ DTO
            loadListSyllabusObjectiveFromSyllabusId(requestLearningObjectiveIds, entity.getId());
            loadListTrainingProgramFromSyllabusId(requestTrainingProgramIds, entity.getId());
            loadListMaterialFromSyllabusId(requestMaterialIds, entity.getId());
            loadListUnitFromListUnitIds(unitIds, entity.getId());
            // Đánh dấu là đã fix
            entity.markModified();
            //save
            syllabusRepository.save(entity);
        } else {
            syllabusDTO.setStatus(true);
            entity = (Syllabus) genericConverter.toEntity(syllabusDTO, Syllabus.class);
            syllabusRepository.save(entity);
            loadListSyllabusObjectiveFromSyllabusId(requestLearningObjectiveIds, entity.getId());
            loadListTrainingProgramFromSyllabusId(requestTrainingProgramIds, entity.getId());
            loadListMaterialFromSyllabusId(requestMaterialIds, entity.getId());
            loadListUnitFromListUnitIds(unitIds, entity.getId());
        }
        SyllabusDTO result = convertSyllabusToSyllabusDTO(entity);
        if (syllabusDTO.getId() == null){
            result.setUnitIds(unitIds);
            result.setTrainingProgramIds(requestTrainingProgramIds);
            result.setLearningObjectiveIds(requestLearningObjectiveIds);
            result.setMaterialIds(requestMaterialIds);
        }
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }

    public void loadListTrainingProgramFromSyllabusId(List<Long> requestTrainingProgramIds, Long syllabusId) {
        if (requestTrainingProgramIds != null && !requestTrainingProgramIds.isEmpty()) {
            for (Long trainingProgramId : requestTrainingProgramIds) {
                TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProgramId).get();
                Syllabus syllabus = syllabusRepository.findOneById(syllabusId);
                if (trainingProgram != null && syllabus != null) {
                    SyllabusTrainingProgram syllabusTrainingProgram = new SyllabusTrainingProgram();
                    syllabusTrainingProgram.setTrainingProgram(trainingProgram);
                    syllabusTrainingProgram.setSyllabus(syllabus);
                    syllabusTrainingProgramRepository.save(syllabusTrainingProgram);
                }
            }
        }
    }

    public Boolean checkExist(Long id){
       Syllabus syllabus = syllabusRepository.findOneById(id);
        return syllabus != null;
    }

    private void loadListSyllabusObjectiveFromSyllabusId(List<Long> requestLearningObjectiveIds, Long syllabusId) {
        if (requestLearningObjectiveIds != null && !requestLearningObjectiveIds.isEmpty()) {
            for (Long learningObjectiveId : requestLearningObjectiveIds) {
                LearningObjective learningObjective = learningObjectiveRepository.findById(learningObjectiveId);
                Syllabus syllabus = syllabusRepository.findOneById(syllabusId);
                if (learningObjective != null && syllabus != null) {
                    SyllabusObjective syllabusObjective = new SyllabusObjective();
                    syllabusObjective.setLearningObjective(learningObjective);
                    syllabusObjective.setSyllabus(syllabus);
                    syllabusObjectiveRepository.save(syllabusObjective);
                }
            }
        }
    }

    private void loadListMaterialFromSyllabusId(List<Long> requestMaterialIds, Long syllabusId) {
        if (requestMaterialIds != null && !requestMaterialIds.isEmpty()) {
            for (Long materialId : requestMaterialIds) {
                Material material = materialRepository.findById(materialId);
                Syllabus syllabus = syllabusRepository.findOneById(syllabusId);
                if (material != null && syllabus != null) {
                    SyllabusMaterial syllabusMaterial = new SyllabusMaterial();
                    syllabusMaterial.setMaterial(material);
                    syllabusMaterial.setSyllabus(syllabus);
                    syllabusMaterialRepository.save(syllabusMaterial);
                }
            }
        }
    }
    private void loadListUnitFromListUnitIds(List<Long> unitIds, Long syllabusId) {
        if (unitIds != null && !unitIds.isEmpty()) {
            Syllabus syllabus = syllabusRepository.findOneById(syllabusId);
            if (syllabus != null) {
                unitIds.stream()
                        .map(unitRepository::findById)
                        .filter(Objects::nonNull)
                        .peek(unit-> unit.setSyllabus(syllabus))
                        .forEach(unitRepository::save);
            }

        }
    }

    private Syllabus cloneSyllabus(Syllabus syllabus){
        Syllabus clone = new Syllabus();
        try {
            BeanUtils.copyProperties(clone, syllabus);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
        return clone;
    }

    private Syllabus fillMissingAttribute(Syllabus entity, Syllabus tempOldEntity){
        List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = entity.getClass();
        try {


// Traverse class hierarchy to collect fields from all superclasses
            while (currentClass != null) {
                Field[] declaredFields = currentClass.getDeclaredFields();
                allFields.addAll(Arrays.asList(declaredFields));
                currentClass = currentClass.getSuperclass();
            }

            // Iterate over all fields
            for (Field field : allFields) {
                field.setAccessible(true); // Enable access to private fields if any

                try {
                    Object newValue = field.get(entity); // Get the value of the field for the newEntity
                    if (newValue == null) {
                        // If the value is null, get the corresponding value from oldEntity
                        Object oldValue = field.get(tempOldEntity);
                        field.set(entity, oldValue); // Set the value of the field for the newEntity
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return entity;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ResponseEntity<?> changeStatus(Long id) {
        Syllabus entity = syllabusRepository.findOneById(id);
        if (entity != null) {
            if (entity.getStatus()) {
                entity.setStatus(false);
            } else {
                entity.setStatus(true);
            }
            syllabusRepository.save(entity);
            return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
        } else {
            return ResponseUtil.error("Syllabus not found", "Cannot change status of non-existing Syllabus", HttpStatus.NOT_FOUND);
        }
    }




    @Override
    public ResponseEntity<?> searchSortFilter(SyllabusDTO syllabusDTO, String sortByCreatedDate, int page, int limit) {
        String name = syllabusDTO.getName();
        String code = syllabusDTO.getCode();

        String description = syllabusDTO.getDescription();
        Boolean isApproved = syllabusDTO.getIsApproved();
        Boolean isActive = syllabusDTO.getIsActive();
        String version = syllabusDTO.getVersion();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Syllabus> entities = syllabusRepository.searchSortFilter(name, code, description, isApproved, isActive, version, sortByCreatedDate, pageable);
        List<SyllabusDTO> result = new ArrayList<>();
        Long count = syllabusRepository.countSearchSortFilter(name, code, description, isApproved, isActive, version);
        convertListSyllabusToListSyllabusDTO(entities, result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                count);
    }

    @Override
    public ResponseEntity<?> searchSortFilterADMIN(SyllabusDTO syllabusDTO, String sortById, int page, int limit) {
        String name = syllabusDTO.getName();
        String code = syllabusDTO.getCode();
        String description = syllabusDTO.getDescription();
        Boolean isApproved = syllabusDTO.getIsApproved();
        Boolean isActive = syllabusDTO.getIsActive();
        String version = syllabusDTO.getVersion();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Syllabus> entities = syllabusRepository.searchSortFilterADMIN(name, code, description, isApproved, isActive, version, sortById, pageable);
        List<SyllabusDTO> result = new ArrayList<>();
        Long count = syllabusRepository.countSearchSortFilter(name, code, description, isApproved, isActive, version);
        convertListSyllabusToListSyllabusDTO(entities, result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                count);
    }



    public void convertListSyllabusToListSyllabusDTO(List<Syllabus> syllabusList, List<SyllabusDTO> syllabusDTOS){
        syllabusDTOS.addAll(syllabusList.stream()
                .map(this::convertSyllabusToSyllabusDTO)
                .toList());
    }

    public SyllabusDTO convertSyllabusToSyllabusDTO(Syllabus syllabus){
        SyllabusDTO newDTO = (SyllabusDTO) genericConverter.toDTO(syllabus, SyllabusDTO.class);
        List<TrainingProgram> trainingProgramList = syllabusRepository.findTrainingProgramsBySyllabusId(syllabus.getId());
        List<Material> materialList = syllabusRepository.findMaterialsBySyllabusId(syllabus.getId());
        List<LearningObjective> learningObjectiveList = syllabusRepository.findLearningObjectivesBySyllabusId(syllabus.getId());
        List<Unit> unitList = syllabusRepository.findUnitsBySyllabusId(syllabus.getId());

        if (trainingProgramList == null) newDTO.setTrainingProgramIds(null);
        else {
            List<Long> trainingProgramIds = trainingProgramList.stream()
                    .map(TrainingProgram::getId)
                    .toList();
            newDTO.setTrainingProgramIds(trainingProgramIds);
        }
        if (materialList == null) newDTO.setMaterialIds(null);
        else {
            List<Long> materialIds = materialList.stream()
                    .map(Material::getId)
                    .toList();
            newDTO.setMaterialIds(materialIds);
        }
        if (learningObjectiveList == null) newDTO.setLearningObjectiveIds(null);
        else {
            List<Long> learningObjectiveIds = learningObjectiveList.stream()
                    .map(LearningObjective::getId)
                    .toList();
            newDTO.setLearningObjectiveIds(learningObjectiveIds);
        }
        if (unitList == null) newDTO.setUnitIds(null);
        else {
            List<Long> outputStandardIds = unitList.stream()
                    .flatMap(unit -> unit.getContents().stream()) // Flattening the list of contents
                    .flatMap(content -> content.getOutputStandards().stream()) // Getting output standards for each content
                    .map(OutputStandard::getId) // Getting the output standard name
                    .toList(); // Collecting them into a list
            Long duration = unitList.stream()
                    .flatMap(unit -> unit.getContents().stream())
                    .mapToLong(Content::getDuration)
                    .sum();
            Map<DeliveryType, Long> timeAllocations = unitList.stream()
                    .flatMap(unit -> unit.getContents().stream())
                    .collect(Collectors.groupingBy(
                            Content::getDeliveryType,
                            Collectors.summingLong(Content::getDuration)
                    ));
            List<Long> unitIds = unitList.stream().map(Unit::getId).toList();
            newDTO.setOutputStandardIds(outputStandardIds);
            newDTO.setUnitIds(unitIds);
            newDTO.setDuration(duration);
            newDTO.setTimeAllocations(timeAllocations);
        }
        return newDTO;
    }




    public List<SyllabusDTO> parseCsvFile(MultipartFile file) throws IOException {
        List<SyllabusDTO> syllabusList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Bỏ qua dòng đầu tiên
                }
//                if((line).equals("")){
//                    continue;
//                }

                String[] data = line.split(","); // Phân cách dữ liệu theo dấu ','

                SyllabusDTO syllabusDTO = new SyllabusDTO();
                syllabusDTO.setName(data[0]);
                syllabusDTO.setCode(data[1]);
                syllabusDTO.setDescription(data[2]);
                syllabusDTO.setIsApproved(Boolean.parseBoolean(data[3]));
                syllabusDTO.setIsActive(Boolean.parseBoolean(data[4]));
                syllabusDTO.setVersion(data[5]);
                syllabusDTO.setAttendee(Long.parseLong(data[6]));


                if(!data[7].equals("null")){
                        String[] unitIds = data[7].split("/");
                        List<Long> unitId = new ArrayList<>();
                        for(String un : unitIds){
                            unitId.add(Long.valueOf(un));
                        }

                        syllabusDTO.setUnitIds(unitId);

                }
                if(!data[8].equals("null")){
                    String[] learningObjectiveIds = data[8].split("/");
                    List<Long> leobId = new ArrayList<>();
                    for(String leob : learningObjectiveIds){
                        leobId.add(Long.valueOf(leob));
                    }
                    syllabusDTO.setLearningObjectiveIds(leobId);
                }
                if(!data[9].equals("null")){
                    String[] materialIds = data[9].split("/");
                    List<Long> maId = new ArrayList<>();
                    for(String ma : materialIds){
                        maId.add(Long.valueOf(ma));
                    }
                    syllabusDTO.setMaterialIds(maId);
                }
                if(!data[10].equals("null")){
                    String[] trainingProgramIds = data[10].split("/");
                    List<Long> trpoId = new ArrayList<>();
                    for(String trpo : trainingProgramIds){
                        trpoId.add(Long.valueOf(trpo));
                    }
                    syllabusDTO.setTrainingProgramIds(trpoId);
                }
//                if(!data[11].equals("null")){
//                    String[] outputStandardIds = data[11].split("/");
//                    List<Long> outputStdIds = new ArrayList<>();
//                    for(String outputStd : outputStandardIds){
//                        outputStdIds.add(Long.valueOf(outputStd));
//                    }
//                    syllabusDTO.setOutputStandardIds(outputStdIds);
//                }
                syllabusList.add(syllabusDTO);

            }
            return syllabusList;
        } catch (Exception e) {
            // Xử lý các trường hợp ngoại lệ nếu có
             e.printStackTrace();
             for( SyllabusDTO sy : syllabusList){
                 syllabusList.remove(sy);
             }
             return syllabusList;
        }


    }

    @Override
    public ResponseEntity<?> checkCsvFile(MultipartFile file) throws IOException {
    List<SyllabusDTO> syllabusList = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
        String line;
        boolean isFirstLine = true;
        while ((line = reader.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;
                continue; // Bỏ qua dòng đầu tiên
            }
//            if((line).equals("")){
//                continue;
//            }

            String[] data = line.split(","); // Phân cách dữ liệu theo dấu ','

            SyllabusDTO syllabusDTO = new SyllabusDTO();
            syllabusDTO.setName(data[0]);
            syllabusDTO.setCode(data[1]);
            syllabusDTO.setDescription(data[2]);
            syllabusDTO.setIsApproved(Boolean.parseBoolean(data[3]));
            syllabusDTO.setIsActive(Boolean.parseBoolean(data[4]));
            syllabusDTO.setVersion(data[5]);
            syllabusDTO.setAttendee(Long.parseLong(data[6]));


            if(!data[7].equals("null")){

                try{
                    String[] unitIds = data[7].split("/");
                    List<Long> unitId = new ArrayList<>();
                    for(String un : unitIds){
                        unitId.add(Long.valueOf(un));
                    }

                    syllabusDTO.setUnitIds(unitId);
                }catch (Exception e){
                    return ResponseUtil.error("Please check format file", e.getMessage(), HttpStatus.BAD_REQUEST);
                }

            }
            if(!data[8].equals("null")){
                try {
                    String[] learningObjectiveIds = data[8].split("/");
                    List<Long> leobId = new ArrayList<>();
                    for(String leob : learningObjectiveIds){
                        leobId.add(Long.valueOf(leob));
                    }
                    syllabusDTO.setLearningObjectiveIds(leobId);
                }catch (Exception e){
                    return ResponseUtil.error("Please check format file", e.getMessage(), HttpStatus.BAD_REQUEST);
                }

            }
            if(!data[9].equals("null")){
                try{
                    String[] materialIds = data[9].split("/");
                    List<Long> maId = new ArrayList<>();
                    for(String ma : materialIds){
                        maId.add(Long.valueOf(ma));
                    }
                    syllabusDTO.setMaterialIds(maId);
                }catch (Exception e){
                    return ResponseUtil.error("Please check format file", e.getMessage(), HttpStatus.BAD_REQUEST);
                }

            }
            if(!data[10].equals("null")){
                try{
                    String[] trainingProgramIds = data[10].split("/");
                    List<Long> trpoId = new ArrayList<>();
                    for(String trpo : trainingProgramIds){
                        trpoId.add(Long.valueOf(trpo));
                    }
                    syllabusDTO.setTrainingProgramIds(trpoId);
                }catch (Exception e){
                    return ResponseUtil.error("Please check format file", e.getMessage(), HttpStatus.BAD_REQUEST);
                }

            }
//            if(!data[11].equals("null")){
//                try{
//                    String[] outputStandardIds = data[11].split("/");
//                    List<Long> outputStdIds = new ArrayList<>();
//                    for(String outputStdId : outputStandardIds){
//                        outputStdIds.add(Long.valueOf(outputStdId));
//                    }
//                    syllabusDTO.setTrainingProgramIds(outputStdIds);
//                }catch (Exception e){
//                    return ResponseUtil.error("Please check format file", e.getMessage(), HttpStatus.BAD_REQUEST);
//                }
//
//            }

            syllabusList.add(syllabusDTO);
        }
        return ResponseUtil.getObject(syllabusList, HttpStatus.OK, "");
    } catch (Exception e) {
        // Xử lý các trường hợp ngoại lệ nếu có
        return ResponseUtil.getError(syllabusList,HttpStatus.BAD_REQUEST, e.getMessage());
    }

}









    public ResponseEntity<?> checkSyllabusReplace(MultipartFile file, Boolean name, Boolean code) throws IOException {
        //Integer count = 0;
        List<SyllabusDTO> errorSyllabus = new ArrayList<>();
        if(checkCsvFile(file).getStatusCode().toString().equals("200 OK")){
            List<SyllabusDTO> syllabusList = parseCsvFile(file);
            List<Syllabus> syllabusShow = new ArrayList<>();
            if(name.toString().equals("true") && code.toString().equals("false")){
                for(SyllabusDTO syllabusDTO : syllabusList) {
                    List<Syllabus> listNameSyllabus = syllabusRepository.getAllSyllabusByName(syllabusDTO.getName());
                    if(listNameSyllabus.size() == 0){
                        save(syllabusDTO); //add them luon
                    }else if( listNameSyllabus.size() == 1){
                        syllabusDTO.setId(listNameSyllabus.get(0).getId());
                        save(syllabusDTO); // update
                    }else if(listNameSyllabus.size() > 1){
                        syllabusShow.addAll(listNameSyllabus);
                        if(syllabusShow.size() !=0){
                            convertListSyllabusToListSyllabusDTO(syllabusShow, errorSyllabus);
                            return ResponseUtil.getError(errorSyllabus,HttpStatus.BAD_REQUEST,"false");//phai xoa chi con 1
                        }
//                    convertListSyllabusToListSyllabusDTO(listNameSyllabus, errorSyllabus);
//                    return ResponseUtil.getError(errorSyllabus,HttpStatus.BAD_REQUEST,"false");//phai xoa chi con 1
                    }
                }

            }else{
                if(name.toString().equals("false") && code.toString().equals("true")){
                    for(SyllabusDTO syllabusDTO : syllabusList){
                        List<Syllabus> listCodeSyllabus = syllabusRepository.getAllSyllabusByCode(syllabusDTO.getCode());
                        if(listCodeSyllabus.size() == 0){
                            save(syllabusDTO); //add them luon
                        }else if( listCodeSyllabus.size() == 1){
                            syllabusDTO.setId(listCodeSyllabus.get(0).getId());
                            save(syllabusDTO); // update
                        }else if(listCodeSyllabus.size() > 1){
                            syllabusShow.addAll(listCodeSyllabus);
                            if(syllabusShow.size() !=0){
                                convertListSyllabusToListSyllabusDTO(syllabusShow, errorSyllabus);
                                return ResponseUtil.getError(errorSyllabus,HttpStatus.BAD_REQUEST,"false");//phai xoa chi con 1
                            }
//                        convertListSyllabusToListSyllabusDTO(listCodeSyllabus, errorSyllabus);
//                        return ResponseUtil.getError(errorSyllabus,HttpStatus.BAD_REQUEST,"false");//phai xoa chi con 1
                        }
                    }

                }else{
                    if(name.toString().equals("true") && code.toString().equals("true")) {
                        for(SyllabusDTO syllabusDTO : syllabusList){
                            List<Syllabus> listNameAndCodeSyllabus = syllabusRepository.getAllSyllabusByNameAndCode(syllabusDTO.getName(), syllabusDTO.getCode());
                            if(listNameAndCodeSyllabus.size() == 0){
                                save(syllabusDTO); //add them luon
                            }else if( listNameAndCodeSyllabus.size() == 1){
                                syllabusDTO.setId(listNameAndCodeSyllabus.get(0).getId());
                                save(syllabusDTO); // update
                            }else if(listNameAndCodeSyllabus.size() > 1){
                                syllabusShow.addAll(listNameAndCodeSyllabus);
                                if(syllabusShow.size() !=0){
                                    convertListSyllabusToListSyllabusDTO(syllabusShow, errorSyllabus);
                                    return ResponseUtil.getError(errorSyllabus,HttpStatus.BAD_REQUEST,"false");//phai xoa chi con 1
                                }
//                        convertListSyllabusToListSyllabusDTO(listCodeSyllabus, errorSyllabus);
//                        return ResponseUtil.getError(errorSyllabus,HttpStatus.BAD_REQUEST,"false");//phai xoa chi con 1
                            }
                        }

                    }
                }
            }
            return ResponseUtil.getObject(null, HttpStatus.OK, "Saved successfully");
        }else {
            return ResponseUtil.error("Please check format file", "", HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<?> checkSyllabusSkip(MultipartFile file, Boolean name, Boolean code) throws IOException {
        //Integer count = 0;
        //List<SyllabusDTO> errorSyllabus = new ArrayList<>();
        if(checkCsvFile(file).getStatusCode().toString().equals("200 OK")){
            List<SyllabusDTO> syllabusList = parseCsvFile(file);
            //List<Syllabus> syllabusShow = new ArrayList<>();
            if(name.toString().equals("true") && code.toString().equals("false")){
                for(SyllabusDTO syllabusDTO : syllabusList) {
                    List<Syllabus> listNameSyllabus = syllabusRepository.getAllSyllabusByName(syllabusDTO.getName());
                    if(listNameSyllabus.size() == 0){
                        save(syllabusDTO); //add them luon
                    }
                }
            }else{
                if(name.toString().equals("false") && code.toString().equals("true")){
                    for(SyllabusDTO syllabusDTO : syllabusList) {
                        List<Syllabus> listCodeSyllabus = syllabusRepository.getAllSyllabusByCode(
                            syllabusDTO.getCode());
                        if (listCodeSyllabus.size() == 0 ) {
                            save(syllabusDTO); //add them luon
                        }
                    }
                }else{
                    if(name.toString().equals("true") && code.toString().equals("true")) {
                        for(SyllabusDTO syllabusDTO : syllabusList){
                            List<Syllabus> listNameAndCodeSyllabus = syllabusRepository.getAllSyllabusByNameAndCode(syllabusDTO.getName(), syllabusDTO.getCode());
                            if(listNameAndCodeSyllabus.size() == 0 ){
                                save(syllabusDTO); //add them luon
                            }
                        }
                    }
                }
            }
            return ResponseUtil.getObject(null, HttpStatus.OK, "Saved successfully");
        }else{
            return ResponseUtil.error("Please check format file", "", HttpStatus.BAD_REQUEST);
        }

    }



    @Override
    public ResponseEntity<?> changeStatusforUpload(DeleteReplace ids, Boolean name, Boolean code) {

        boolean flag = false;
        for (Long id : ids.getId()) {
            Syllabus entity = syllabusRepository.findOneById(id);
            if (entity != null) {
                if (name.toString().equals("true") && code.toString().equals("false")) {
                    List<Syllabus> syllabusListName = syllabusRepository.getAllSyllabusByName(
                        entity.getName());
                    if(ids.getId().size() == syllabusListName.size() && flag == false){
                        return ResponseUtil.error("false",
                            "Don't remove all, leave one id", HttpStatus.NOT_FOUND);
                    }
                    flag = true;
                    if (syllabusListName.size() != 1) {
                        if (entity.getStatus()) {
                            entity.setStatus(false);
                        } else {
                            entity.setStatus(true);
                        }
                        syllabusRepository.save(entity);
                    } else {
                        return ResponseUtil.error("false",
                            "Cannot change status of non-existing Syllabus", HttpStatus.NOT_FOUND);
                    }


                } else {
                    if (name.toString().equals("false") && code.toString().equals("true")) {
                        List<Syllabus> syllabusListCode = syllabusRepository.getAllSyllabusByCode(
                            entity.getCode());
                        if(ids.getId().size() == syllabusListCode.size() && flag == false){
                            return ResponseUtil.error("false",
                                "Don't remove all, leave one id", HttpStatus.NOT_FOUND);
                        }
                        flag = true;
                        if (syllabusListCode.size() != 1) {
                            if (entity.getStatus()) {
                                entity.setStatus(false);
                            } else {
                                entity.setStatus(true);
                            }
                            syllabusRepository.save(entity);
                        } else {
                            return ResponseUtil.error("false",
                                "Cannot change status of non-existing Syllabus",
                                HttpStatus.NOT_FOUND);
                        }


                    } else {
                        if (name.toString().equals("true") && code.toString().equals("true")) {
                            List<Syllabus> syllabusListNameAndCode = syllabusRepository.getAllSyllabusByNameAndCode(
                                entity.getName(), entity.getCode());
                            if(ids.getId().size() == syllabusListNameAndCode.size() && flag == false){
                                return ResponseUtil.error("false",
                                    "Don't remove all, leave one id", HttpStatus.NOT_FOUND);
                            }
                            flag = true;
                            if (syllabusListNameAndCode.size() != 1) {
                                if (entity.getStatus()) {
                                    entity.setStatus(false);
                                } else {
                                    entity.setStatus(true);
                                }
                                syllabusRepository.save(entity);
                            } else {
                                return ResponseUtil.error("false",
                                    "Cannot change status of non-existing Syllabus",
                                    HttpStatus.NOT_FOUND);
                            }


                        }
                    }
                }
            } else {
                return ResponseUtil.error("Syllabus not found",
                    "Cannot change status of non-existing Syllabus", HttpStatus.NOT_FOUND);
            }
        }

        return ResponseUtil.getObject(null, HttpStatus.CREATED, "Delete Susscessfully");

    }





}
