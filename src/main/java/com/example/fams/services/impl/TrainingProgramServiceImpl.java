package com.example.fams.services.impl;

import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.dto.request.DeleteReplace;
import com.example.fams.entities.*;

import com.example.fams.repository.SyllabusRepository;
import com.example.fams.repository.SyllabusTrainingProgramRepository;
import com.example.fams.repository.TrainingProgramRepository;
import com.example.fams.services.ITrainingProgramService;
import com.example.fams.services.ServiceUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Service("TrainingProgramService")
public class TrainingProgramServiceImpl implements ITrainingProgramService {
    @Autowired
    private TrainingProgramRepository trainingProgramRepository;

    @Autowired
    private SyllabusRepository syllabusRepository;

    @Autowired
    private SyllabusTrainingProgramRepository syllabusTrainingProgramRepository;

    @Autowired
    private GenericConverter genericConverter;
    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        List<TrainingProgramDTO> result = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<TrainingProgram> trainingPrograms = trainingProgramRepository.findAllByStatusIsTrue(pageable);


        convertListTpToListTpDTO(trainingPrograms, result);

        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                trainingProgramRepository.countAllByStatusIsTrue());
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {
        try {
            Pageable pageable = PageRequest.of(page - 1, limit);
            List<TrainingProgram> trainingPrograms = trainingProgramRepository.findAll(pageable).getContent();

            List<TrainingProgramDTO> dtos = new ArrayList<>();
            convertListTpToListTpDTO(trainingPrograms, dtos);

            long totalCount = trainingProgramRepository.count();
            return ResponseUtil.getCollection(dtos, HttpStatus.OK, "Fetched successfully", page, limit, totalCount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching training programs: " + e.getMessage());
        }
    }


    @Override
    public ResponseEntity<?> findById(Long id) {
        try {
            // Attempt to find the training program by its ID
            TrainingProgram entity = trainingProgramRepository.findByStatusIsTrueAndId(id);

            // Check if the training program exists
            if (entity != null) {
                // Convert the entity to DTO
                TrainingProgramDTO dto = convertTpToTpDTO(entity);
                // Return a successful response with the DTO
                return ResponseUtil.getObject(dto, HttpStatus.OK, "Fetched successfully");
            } else {
                // If the training program does not exist, return a not found response
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Training program not found with ID: " + id);
            }
        } catch (Exception e) {
            // If an error occurs during the process, return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching training program: " + e.getMessage());
        }
    }




    @Override
    public ResponseEntity<?> save(TrainingProgramDTO trainingProgramDTO) {



            ServiceUtils.errors.clear();
            // Extract content DTOs from training program DTO
            List<Long> requestSyllabusIds = trainingProgramDTO.getSyllabusIds();

            // Initialize entity
            TrainingProgram entity;

            // * Validate requestDTO ( if left null, then can be updated later )
            if (requestSyllabusIds != null){
                ServiceUtils.validateSyllabusIds(requestSyllabusIds, syllabusRepository);
            }
            if (!ServiceUtils.errors.isEmpty()) {
                throw new CustomValidationException(ServiceUtils.errors);
            }


            // Check if the training program already exists (for update request)
            if (trainingProgramDTO.getId() != null) {
                // Find the existing training program entity by ID
                TrainingProgram oldEntity = trainingProgramRepository.findOneById(trainingProgramDTO.getId());
                // Make a clone of the existing entity
                TrainingProgram tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
                // Update the existing entity with the data from the DTO
                entity = convertDtoToEntity(trainingProgramDTO, syllabusTrainingProgramRepository);
                // Fill missing attributes in the updated entity
                entity = ServiceUtils.fillMissingAttribute(entity, tempOldEntity);
                // Delete existing associations between training program and syllabus
                syllabusTrainingProgramRepository.deleteAllByTrainingProgramId(trainingProgramDTO.getId());
                // Load new associations between training program and syllabus
                loadTrainingProgramSyllabusFromListSyllabus(requestSyllabusIds, entity.getId());
                // Mark the entity as modified
                entity.markModified();
                trainingProgramRepository.save(entity);
            } else {
                // For new training program creation
                entity = convertDtoToEntity(trainingProgramDTO, syllabusTrainingProgramRepository);
                entity.setStatus(true);
                trainingProgramRepository.save(entity);
                loadTrainingProgramSyllabusFromListSyllabus(requestSyllabusIds, entity.getId());
            }

            TrainingProgramDTO result = convertTpToTpDTO(entity);

            // Return a success response with the saved entity
            return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");

    }

    private void loadTrainingProgramSyllabusFromListSyllabus(List<Long> requestyllabusIds, Long trainingProgramId) {
        if (requestyllabusIds != null && !requestyllabusIds.isEmpty()) {
            TrainingProgram trainingProgram = trainingProgramRepository.findOneById(trainingProgramId);
            if (trainingProgram != null) {
                for (Long syllabusId : requestyllabusIds) {
                    Syllabus syllabus = syllabusRepository.findOneById(syllabusId);
                    if (syllabus != null) {
                        SyllabusTrainingProgram loc = new SyllabusTrainingProgram();
                        loc.setTrainingProgram(trainingProgram);
                        loc.setSyllabus(syllabus);
                        syllabusTrainingProgramRepository.save(loc);
                    }
                }
            }
        }
    }

    @Override
    public ResponseEntity<?> changeStatus(Long id) {
        TrainingProgram entity = trainingProgramRepository.findOneById(id);
        if (entity != null) {
            if (entity.getStatus()) {
                entity.setStatus(false);
            } else {
                entity.setStatus(true);
            }
            trainingProgramRepository.save(entity);
            return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
        } else {
            return ResponseUtil.error("LearningObjective not found", "Cannot change status of non-existing LearningObjective", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Boolean checkEixst(Long id) {
        TrainingProgram trainingProgram = trainingProgramRepository.findOneById(id);
        return trainingProgram != null;
    }

    @Override
    public ResponseEntity<?> searchSortFilter(TrainingProgramDTO trainingProgramDTO, String sortByCreatedDate, int page, int limit) {
        String name = trainingProgramDTO.getName();
        Long startTime = trainingProgramDTO.getStartTime();
        Long duration = trainingProgramDTO.getDuration();
        Integer training_status = trainingProgramDTO.getTraining_status();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<TrainingProgram> entities = trainingProgramRepository.searchSortFilter(name, startTime, duration, training_status, sortByCreatedDate, pageable);
        List<TrainingProgramDTO> result = new ArrayList<>();
        convertListTpToListTpDTO(entities, result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                result.size() + 1);
    }

    @Override
    public ResponseEntity<?> searchSortFilterADMIN(TrainingProgramDTO trainingProgramDTO, String sortById, int page, int limit) {
        String name = trainingProgramDTO.getName();
        Long startTime = trainingProgramDTO.getStartTime();
        Long duration = trainingProgramDTO.getDuration();
        Integer training_status = trainingProgramDTO.getTraining_status();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<TrainingProgram> entities = trainingProgramRepository.searchSortFilterADMIN(name, startTime, duration, training_status, sortById,  pageable);
        List<TrainingProgramDTO> result = new ArrayList<>();
        convertListTpToListTpDTO(entities, result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                result.size() + 1);
    }

    private TrainingProgram convertDtoToEntity(TrainingProgramDTO trainingProgramDTO, SyllabusTrainingProgramRepository syllabusTrainingProgramRepository) {
        TrainingProgram trainingProgram = new TrainingProgram();
        trainingProgram.setId(trainingProgramDTO.getId());
        trainingProgram.setName(trainingProgramDTO.getName());
        trainingProgram.setStartTime(trainingProgramDTO.getStartTime());
        trainingProgram.setDuration(trainingProgramDTO.getDuration());
        trainingProgram.setTraining_status(trainingProgramDTO.getTraining_status());
        trainingProgram.setStatus(trainingProgramDTO.getStatus());

        return trainingProgram;
    }

    private void convertListTpToListTpDTO(List<TrainingProgram> entities, List<TrainingProgramDTO> result) {
        for (TrainingProgram tp : entities){
            TrainingProgramDTO trainingProgramDTO = convertTpToTpDTO(tp);
            result.add(trainingProgramDTO);
        }
    }

    private TrainingProgramDTO convertTpToTpDTO(TrainingProgram entity) {
        TrainingProgramDTO newTpDTO = (TrainingProgramDTO) genericConverter.toDTO(entity, TrainingProgramDTO.class);
        List<Syllabus> syllabus = syllabusTrainingProgramRepository.findSyllabusByTrainingProgramId(entity.getId());
        if (entity.getSyllabusTrainingPrograms() == null){
            newTpDTO.setSyllabusIds(null);
        }
        else {
            // ! Set list learningObjectiveIds và unitId sau khi convert ở trên vào contentDTO
            List<Long> syllabusIds = syllabus.stream()
                    .map(Syllabus::getId)
                    .toList();

            newTpDTO.setSyllabusIds(syllabusIds);

        }
        return newTpDTO;
    }

    public List<TrainingProgramDTO> parseExcelFile(MultipartFile file) throws IOException {
        List<TrainingProgramDTO> trainingProgramDTOS = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
                XSSFRow row = worksheet.getRow(index);
                TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();





                        //trainingProgramDTO.setId(Long.valueOf(getCellValueAsString(row.getCell(0))));
                        trainingProgramDTO.setName(getCellValueAsString(row.getCell(0)));
                        trainingProgramDTO.setStartTime(Long.valueOf(getCellValueAsString(row.getCell(1))));
                        trainingProgramDTO.setDuration(Long.valueOf(getCellValueAsString(row.getCell(2))));
                        trainingProgramDTO.setTraining_status(Integer.valueOf(getCellValueAsString(row.getCell(3))));
                      //  trainingProgramDTO.setStatus(Boolean.valueOf(getCellValueAsString(row.getCell(4))));
                    if(!getCellValueAsString(row.getCell(4)).isEmpty()){
                        String[] syllabusIds = getCellValueAsString(row.getCell(4)).split(",");
                        List<Long> syId = new ArrayList<>();
                        for(String sy : syllabusIds){
                            syId.add(Long.valueOf(sy));
                        }
                        trainingProgramDTO.setSyllabusIds(syId);
                    }

                        trainingProgramDTOS.add(trainingProgramDTO);



            }
        }

        workbook.close();
        return trainingProgramDTOS;
    }

//    @Override
//    public ResponseEntity<?> checkCsvFile(MultipartFile file) throws IOException {
//        List<TrainingProgramDTO> trainingProgramList = new ArrayList<>();
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            String line;
//            boolean isFirstLine = true;
//            while (!(line = reader.readLine()).equals("")) {
//                if (isFirstLine) {
//                    isFirstLine = false;
//                    continue; // Bỏ qua dòng đầu tiên
//                }
//
//                String[] data = line.split(","); // Phân cách dữ liệu theo dấu ','
//
//                TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
//                trainingProgramDTO.setName(data[0]);
//                trainingProgramDTO.setDuration(Long.valueOf(data[1]));
//                trainingProgramDTO.setTraining_status(Integer.valueOf(data[2]));
//
//
//                if(!data[3].equals("null")){
//
//                    try{
//                        String[] syllabusIds = data[3].split("/");
//                        List<Long> syllabusId = new ArrayList<>();
//                        for(String sy : syllabusIds){
//                            syllabusId.add(Long.valueOf(sy));
//                        }
//
//                        trainingProgramDTO.setSyllabusIds(syllabusId);
//                    }catch (Exception e){
//                        return ResponseUtil.error("Please check format file", e.getMessage(), HttpStatus.BAD_REQUEST);
//                    }
//
//                }
//
//                trainingProgramList.add(trainingProgramDTO);
//            }
//            return ResponseUtil.getObject(trainingProgramList, HttpStatus.OK, "");
//        } catch (Exception e) {
//            // Xử lý các trường hợp ngoại lệ nếu có
//            return ResponseUtil.getError(trainingProgramList,HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//
//    }


    @Override
    public ResponseEntity<?> checkCsvFile(MultipartFile file) throws IOException {
        List<TrainingProgramDTO> trainingProgramList = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line; // Đọc dòng đầu tiên
        boolean isFirstLine = true;

        while ((line = reader.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;
                continue; // Skip the first line
            }

            // If the line is "end", stop the loop
            if (line.equals("end")) {
                break;
            }

            // If the line is empty, continue to the next line
            if (line.isEmpty()) {
                continue;
            }

            String[] data = line.split(","); // Phân cách dữ liệu theo dấu ','

            TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
            trainingProgramDTO.setName(data[0]);
            trainingProgramDTO.setDuration(Long.valueOf(data[1]));
            trainingProgramDTO.setTraining_status(Integer.valueOf(data[2]));

            if (!data[3].equals("null")) {
                String[] syllabusIds = data[3].split("/");
                List<Long> syllabusId = new ArrayList<>();
                for (String sy : syllabusIds) {
                    try {
                        syllabusId.add(Long.valueOf(sy));
                    } catch (NumberFormatException e) {
                        return ResponseUtil.error("Please check format file", "Invalid format for syllabusIds: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                    }
                }
                trainingProgramDTO.setSyllabusIds(syllabusId);
            }

            trainingProgramList.add(trainingProgramDTO);
        }

        reader.close(); // Đóng luồng đọc tập tin

        return ResponseUtil.getObject(trainingProgramList, HttpStatus.OK, "");
    }


    @Override
    public List<TrainingProgramDTO> parseCsvFile(MultipartFile file) throws IOException {
        List<TrainingProgramDTO> trainingProgramList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the first line
                }

                // If the line is "end", stop the loop
                if (line.equals("end")) {
                    break;
                }

                // If the line is empty, continue to the next line
                if (line.isEmpty()) {
                    continue;
                }

                String[] data = line.split(","); // Phân cách dữ liệu theo dấu ','

                TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
                trainingProgramDTO.setName(data[0]);
                trainingProgramDTO.setDuration(Long.parseLong(data[1]));
                trainingProgramDTO.setTraining_status(Integer.parseInt(data[2]));


                if(!data[3].equals("null")){
                    String[] syllabusIds = data[3].split("/");
                    List<Long> syllabusId = new ArrayList<>();
                    for(String sy : syllabusIds){
                        syllabusId.add(Long.valueOf(sy));
                    }

                    trainingProgramDTO.setSyllabusIds(syllabusId);

                }

                trainingProgramList.add(trainingProgramDTO);
            }
            return trainingProgramList;
        } catch (Exception e) {
            // Xử lý các trường hợp ngoại lệ nếu có
            e.printStackTrace();
            for( TrainingProgramDTO sy : trainingProgramList){
                trainingProgramList.remove(sy);
            }
            return trainingProgramList;
        }
    }

    @Override
    public ResponseEntity<?> checkTrainingProgramReplace(MultipartFile file, Boolean id, Boolean name)
        throws IOException {
        List<TrainingProgramDTO> errorTraningProgram = new ArrayList<>();
        if(checkCsvFile(file).getStatusCode().toString().equals("200 OK")){
            List<TrainingProgramDTO> trainingProgramList = parseCsvFile(file);
            List<TrainingProgram> tranProgShow = new ArrayList<>();
            if(name.toString().equals("true") && id.toString().equals("false")){
                for(TrainingProgramDTO trainingProgramDTO : trainingProgramList) {
                    List<TrainingProgram> listNameTranPro = trainingProgramRepository.getAllTrainingProgramByName(trainingProgramDTO.getName());
                    if(listNameTranPro.size() == 0){
                        save(trainingProgramDTO); //add them luon
                    }else if( listNameTranPro.size() == 1){
                        trainingProgramDTO.setId(listNameTranPro.get(0).getId());
                        save(trainingProgramDTO); // update
                    }else if(listNameTranPro.size() > 1){
                        tranProgShow.addAll(listNameTranPro);
                        if(tranProgShow.size() !=0){
                            convertListTpToListTpDTO(tranProgShow, errorTraningProgram);
                            return ResponseUtil.getError(errorTraningProgram,HttpStatus.BAD_REQUEST,"false");//phai xoa chi con 1
                        }
//                    convertListSyllabusToListSyllabusDTO(listNameSyllabus, errorSyllabus);
//                    return ResponseUtil.getError(errorSyllabus,HttpStatus.BAD_REQUEST,"false");//phai xoa chi con 1
                    }
                }

            }else{
                if(name.toString().equals("false") && id.toString().equals("true")){
                    for(TrainingProgramDTO trainingProgramDTO : trainingProgramList){
                        List<TrainingProgram> listCodeTranPro = trainingProgramRepository.getAllTrainingProgramById(trainingProgramDTO.getId());
                        if(listCodeTranPro.size() == 0){
                            save(trainingProgramDTO); //add them luon
                        }else if( listCodeTranPro.size() == 1){
                            trainingProgramDTO.setId(listCodeTranPro.get(0).getId());
                            save(trainingProgramDTO); // update
                        }else if(listCodeTranPro.size() > 1){
                            tranProgShow.addAll(listCodeTranPro);
                            if(tranProgShow.size() !=0){
                                convertListTpToListTpDTO(tranProgShow, errorTraningProgram);
                                return ResponseUtil.getError(errorTraningProgram,HttpStatus.BAD_REQUEST,"false");//phai xoa chi con 1
                            }
//                        convertListSyllabusToListSyllabusDTO(listCodeSyllabus, errorSyllabus);
//                        return ResponseUtil.getError(errorSyllabus,HttpStatus.BAD_REQUEST,"false");//phai xoa chi con 1
                        }
                    }

                }else{
                    if(name.toString().equals("true") && id.toString().equals("true")) {
                        for(TrainingProgramDTO trainingProgramDTO : trainingProgramList){
                            List<TrainingProgram> listNameAndCodeTranPro = trainingProgramRepository.getAllSyllabusByNameAndId(trainingProgramDTO.getId(), trainingProgramDTO.getName());
                            if(listNameAndCodeTranPro.size() == 0){
                                save(trainingProgramDTO); //add them luon
                            }else if( listNameAndCodeTranPro.size() == 1){
                                trainingProgramDTO.setId(listNameAndCodeTranPro.get(0).getId());
                                save(trainingProgramDTO); // update
                            }else if(listNameAndCodeTranPro.size() > 1){
                                tranProgShow.addAll(listNameAndCodeTranPro);
                                if(tranProgShow.size() !=0){
                                    convertListTpToListTpDTO(tranProgShow, errorTraningProgram);
                                    return ResponseUtil.getError(errorTraningProgram,HttpStatus.BAD_REQUEST,"false");//phai xoa chi con 1
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
    public ResponseEntity<?> checkTrainingProgramSkip(MultipartFile file, Boolean id, Boolean name)
        throws IOException {
        if(checkCsvFile(file).getStatusCode().toString().equals("200 OK")){
            List<TrainingProgramDTO> trainingProgramList = parseCsvFile(file);
            //List<Syllabus> syllabusShow = new ArrayList<>();
            if(name.toString().equals("true") && id.toString().equals("false")){
                for(TrainingProgramDTO trainingProgramDTO : trainingProgramList) {
                    List<TrainingProgram> listNameTranPro = trainingProgramRepository.getAllTrainingProgramByName(trainingProgramDTO.getName());
                    if(listNameTranPro.size() == 0){
                        save(trainingProgramDTO); //add them luon
                    }
                }
            }else{
                if(name.toString().equals("false") && id.toString().equals("true")){
                    for(TrainingProgramDTO trainingProgramDTO : trainingProgramList) {
                        List<TrainingProgram> listCodeSyllabus = trainingProgramRepository.getAllTrainingProgramById(
                            trainingProgramDTO.getId());
                        if (listCodeSyllabus.size() == 0 ) {
                            save(trainingProgramDTO); //add them luon
                        }
                    }
                }else{
                    if(name.toString().equals("true") && id.toString().equals("true")) {
                        for(TrainingProgramDTO trainingProgramDTO : trainingProgramList){
                            List<TrainingProgram> listNameAndCodeTranPro = trainingProgramRepository.getAllSyllabusByNameAndId(trainingProgramDTO.getId(), trainingProgramDTO.getName());
                            if(listNameAndCodeTranPro.size() == 0 ){
                                save(trainingProgramDTO); //add them luon
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
    public ResponseEntity<?> changeStatusforUpload(DeleteReplace ids, Boolean id,
        Boolean name) {
        boolean flag =false;
        for (Long idd : ids.getId()) {
            TrainingProgram entity = trainingProgramRepository.findOneById(idd);
            if (entity != null) {
                if (name.toString().equals("true") && id.toString().equals("false")) {
                    List<TrainingProgram> trainingProgramListName = trainingProgramRepository.getAllTrainingProgramByName(
                        entity.getName());
                    if(ids.getId().size() == trainingProgramListName.size() && flag == false){
                        return ResponseUtil.error("false",
                            "Don't remove all, leave one id", HttpStatus.NOT_FOUND);
                    }
                    flag = true;
                    if (trainingProgramListName.size() != 1) {
                        if (entity.getStatus()) {
                            entity.setStatus(false);
                        } else {
                            entity.setStatus(true);
                        }
                        trainingProgramRepository.save(entity);
                    } else {
                        return ResponseUtil.error("false",
                            "Cannot change status of non-existing Syllabus", HttpStatus.NOT_FOUND);
                    }


                } else {
                    if (name.toString().equals("false") && id.toString().equals("true")) {
                        List<TrainingProgram> trainingProgramListId = trainingProgramRepository.getAllTrainingProgramById(
                            entity.getId());
                        if(ids.getId().size() == trainingProgramListId.size() && flag == false){
                            return ResponseUtil.error("false",
                                "Don't remove all, leave one id", HttpStatus.NOT_FOUND);
                        }
                        flag =true;
                        if (trainingProgramListId.size() != 1) {
                            if (entity.getStatus()) {
                                entity.setStatus(false);
                            } else {
                                entity.setStatus(true);
                            }
                            trainingProgramRepository.save(entity);
                        } else {
                            return ResponseUtil.error("false",
                                "Cannot change status of non-existing Syllabus",
                                HttpStatus.NOT_FOUND);
                        }


                    } else {
                        if (name.toString().equals("true") && id.toString().equals("true")) {
                            List<TrainingProgram> trainingProgramListNameAndId = trainingProgramRepository.getAllSyllabusByNameAndId(
                                entity.getId(), entity.getName());
                            if(ids.getId().size() == trainingProgramListNameAndId.size() && flag == false){
                                return ResponseUtil.error("false",
                                    "Don't remove all, leave one id", HttpStatus.NOT_FOUND);
                            }
                            flag=true;
                            if (trainingProgramListNameAndId.size() != 1) {
                                if (entity.getStatus()) {
                                    entity.setStatus(false);
                                } else {
                                    entity.setStatus(true);
                                }
                                trainingProgramRepository.save(entity);
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

    private String getCellValueAsString(XSSFCell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return Long.toString((long) cell.getNumericCellValue());
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
