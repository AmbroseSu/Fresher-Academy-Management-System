package com.example.fams.services.impl;

import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.*;
import com.example.fams.entities.*;
import com.example.fams.repository.*;
import com.example.fams.services.ISyllabusService;
import com.example.fams.services.ServiceUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        SyllabusDTO result = (SyllabusDTO) genericConverter.toDTO(entity, SyllabusDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
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
            entity = convertDtoToEntity(syllabusDTO, syllabusMaterialRepository, syllabusTrainingProgramRepository, syllabusObjectiveRepository);
            // Thêm nhg attribute còn thiếu từ entity cũ vào entity mới
            entity = fillMissingAttribute(entity, tempOldEntity);

            // Xử lí quan hệ
            // Xóa quan hệ cũ trong bảng phụ
            syllabusObjectiveRepository.deleteAllBySyllabusId(syllabusDTO.getId());
            syllabusMaterialRepository.deleteAllBySyllabusId(syllabusDTO.getId());
            syllabusTrainingProgramRepository.deleteAllBySyllabusId(syllabusDTO.getId());

            // Update quan hệ mới từ DTO
            loadListSyllabusObjectiveFromSyllabusId(requestLearningObjectiveIds, entity.getId());
            loadListTrainingProgramFromSyllabusId(requestTrainingProgramIds, entity.getId());
            loadListMaterialFromSyllabusId(requestMaterialIds, entity.getId());
            // Đánh dấu là đã fix
            entity.markModified();
            //save
            syllabusRepository.save(entity);
        } else {
            syllabusDTO.setStatus(true);
            entity = convertDtoToEntity(syllabusDTO, syllabusMaterialRepository, syllabusTrainingProgramRepository, syllabusObjectiveRepository);
            syllabusRepository.save(entity);
            loadListSyllabusObjectiveFromSyllabusId(requestLearningObjectiveIds, entity.getId());
            loadListTrainingProgramFromSyllabusId(requestTrainingProgramIds, entity.getId());
            loadListMaterialFromSyllabusId(requestMaterialIds, entity.getId());
        }
        SyllabusDTO result = convertSyllabusToSyllabusDTO(entity);
//        result.setUnitIds(unitIds);
//        result.setTrainingProgramIds(requestTrainingProgramIds);
//        result.setLearningObjectiveIds(requestLearningObjectiveIds);
//        result.setMaterialIds(requestMaterialIds);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }

    private void loadListTrainingProgramFromSyllabusId(List<Long> requestTrainingProgramIds, Long syllabusId) {
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
    public ResponseEntity<?> searchSortFilter(SyllabusDTO syllabusDTO, int page, int limit) {
        String name = syllabusDTO.getName();
        String code = syllabusDTO.getCode();
        Long timeAllocation = syllabusDTO.getTimeAllocation();
        String description = syllabusDTO.getDescription();
        Boolean isApproved = syllabusDTO.getIsApproved();
        Boolean isActive = syllabusDTO.getIsActive();
        String version = syllabusDTO.getVersion();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Syllabus> entities = syllabusRepository.searchSortFilter(name, code, timeAllocation, description, isApproved, isActive, version, pageable);
        List<SyllabusDTO> result = new ArrayList<>();
        Long count = syllabusRepository.countSearchSortFilter(name, code, timeAllocation, description, isApproved, isActive, version);
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
        Long timeAllocation = syllabusDTO.getTimeAllocation();
        String description = syllabusDTO.getDescription();
        Boolean isApproved = syllabusDTO.getIsApproved();
        Boolean isActive = syllabusDTO.getIsActive();
        String version = syllabusDTO.getVersion();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Syllabus> entities = syllabusRepository.searchSortFilterADMIN(name, code, timeAllocation, description, isApproved, isActive, version, sortById, pageable);
        List<SyllabusDTO> result = new ArrayList<>();
        Long count = syllabusRepository.countSearchSortFilter(name, code, timeAllocation, description, isApproved, isActive, version);
        convertListSyllabusToListSyllabusDTO(entities, result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                count);
    }


    public Syllabus convertDtoToEntity(SyllabusDTO dto, SyllabusMaterialRepository syllabusMaterialRepository,
                                       SyllabusTrainingProgramRepository syllabusTrainingProgramRepository, SyllabusObjectiveRepository syllabusObjectiveRepository) {
        Syllabus syllabus = new Syllabus();
        syllabus.setId(dto.getId());
        syllabus.setName(dto.getName());
        syllabus.setCode(dto.getCode());
        syllabus.setTimeAllocation(dto.getTimeAllocation());
        syllabus.setDescription(dto.getDescription());
        syllabus.setIsApproved(dto.getIsApproved());
        syllabus.setIsActive(dto.getIsActive());
        syllabus.setVersion(dto.getVersion());

//        List<SyllabusMaterial> syllabusMaterials = syllabusMaterialRepository.findAllMaterialBySyllabusId(dto.getId());
//        syllabus.setSyllabusMaterial(syllabusMaterials);
//
//        List<SyllabusTrainingProgram> syllabusTrainingPrograms = syllabusTrainingProgramRepository.findAllTrainingProgramSyllabusBySyllabusId(dto.getId());
//        syllabus.setSyllabusTrainingPrograms(syllabusTrainingPrograms);
//
//        List<SyllabusObjective> syllabusObjectives = syllabusObjectiveRepository.findAllLearingObjectiveBySyllabusId(dto.getId());
//        syllabus.setSyllabusObjectives(syllabusObjectives);
        List<Unit> units = new ArrayList<>();
        if (dto.getUnitIds() != null) {
            for (Long id : dto.getUnitIds()) {
                Unit unit = unitRepository.findById(id);
                if (unit != null) {
                    unit.setSyllabus(syllabus); // Set the syllabus to the unit
                    units.add(unit);
                }
            }
        }
        syllabus.setUnits(units);

        return syllabus;
    }

    private void convertListSyllabusToListSyllabusDTO(List<Syllabus> syllabusList, List<SyllabusDTO> syllabusDTOS){
        for (Syllabus syllabus : syllabusList) {
            SyllabusDTO newDTO = convertSyllabusToSyllabusDTO(syllabus);
            syllabusDTOS.add(newDTO);
        }
    }

    private SyllabusDTO convertSyllabusToSyllabusDTO(Syllabus syllabus){
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
            List<Long> unitIds = unitList.stream().map(Unit::getId).toList();
            newDTO.setUnitIds(unitIds);
        }
        return newDTO;
    }

}
