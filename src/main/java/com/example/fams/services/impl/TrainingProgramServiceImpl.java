package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.SyllabusDTO;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.entities.Syllabus;
import com.example.fams.entities.TrainingProgram;
import com.example.fams.entities.TrainingProgramSyllabus;
import com.example.fams.repository.SyllabusRepository;
import com.example.fams.repository.TrainingProgramRepository;
import com.example.fams.repository.TrainingProgramSyllabusRepository;
import com.example.fams.services.ITrainingProgramService;
import com.example.fams.services.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("TrainingProgramService")
public class TrainingProgramServiceImpl implements ITrainingProgramService {
    @Autowired
    private TrainingProgramRepository trainingProgramRepository;

    @Autowired
    private SyllabusRepository syllabusRepository;

    @Autowired
    private TrainingProgramSyllabusRepository trainingProgramSyllabusRepository;

    @Autowired
    private GenericConverter genericConverter;
    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        List<TrainingProgramDTO> result = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<TrainingProgram> entities = trainingProgramRepository.findAllByStatusIsTrue(pageable);

        for (TrainingProgram entity : entities){
            TrainingProgramDTO dto = (TrainingProgramDTO) genericConverter.toDTO(entity, TrainingProgramDTO.class);
            result.add(dto);
        }
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
            for (TrainingProgram trainingProgram : trainingPrograms) {
                TrainingProgramDTO dto = (TrainingProgramDTO) genericConverter.toDTO(trainingProgram, TrainingProgramDTO.class);
                dtos.add(dto);
            }

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
                TrainingProgramDTO dto = (TrainingProgramDTO) genericConverter.toDTO(entity, TrainingProgramDTO.class);
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
        try {
            // Extract content DTOs from training program DTO
            List<SyllabusDTO> requestSyllabusDTOs = trainingProgramDTO.getSyllabusDTOs();

            // Initialize entity
            TrainingProgram entity;

            // Check if the training program already exists (for update request)
            if (trainingProgramDTO.getId() != null) {
                // Find the existing training program entity by ID
                TrainingProgram oldEntity = trainingProgramRepository.findOneById(trainingProgramDTO.getId());
                // Make a clone of the existing entity
                TrainingProgram tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
                // Update the existing entity with the data from the DTO
                entity = (TrainingProgram) genericConverter.updateEntity(trainingProgramDTO, oldEntity);
                // Fill missing attributes in the updated entity
                entity = ServiceUtils.fillMissingAttribute(entity, tempOldEntity);
                // Delete existing associations between training program and syllabus
                trainingProgramSyllabusRepository.deleteAllByTrainingProgramId(trainingProgramDTO.getId());
                // Load new associations between training program and syllabus
                loadTrainingProgramSyllabusFromListSyllabus(requestSyllabusDTOs, entity.getId());
                // Mark the entity as modified
                entity.markModified();
                trainingProgramRepository.save(entity);
            } else {
                // For new training program creation
                entity = (TrainingProgram) genericConverter.toEntity(trainingProgramDTO, TrainingProgram.class);
                entity.setStatus(true);
                trainingProgramRepository.save(entity);
                loadTrainingProgramSyllabusFromListSyllabus(requestSyllabusDTOs, entity.getId());
            }

           TrainingProgramDTO result = (TrainingProgramDTO) genericConverter.toDTO(entity, TrainingProgramDTO.class);

           List<Syllabus> syllabusList = trainingProgramSyllabusRepository.findSyllabusByTrainingProgramId(entity.getId());
           List<SyllabusDTO> syllabusDTOS = new ArrayList<>();
            for (Syllabus syllabus: syllabusList
                 ) {
                SyllabusDTO newSyllabusDTO = (SyllabusDTO) genericConverter.toDTO(syllabus, SyllabusDTO.class);
                syllabusDTOS.add(newSyllabusDTO);

            }
            result.setSyllabusDTOs(syllabusDTOS);

            // Return a success response with the saved entity
            return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
        } catch (Exception e) {
            // Return an error response if an exception occurs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving training program: " + e.getMessage());
        }
    }



    private void loadTrainingProgramSyllabusFromListSyllabus(List<SyllabusDTO> requestyllabusDTOs, Long trainingProgramId) {
        if (requestyllabusDTOs != null && !requestyllabusDTOs.isEmpty()) {
            for (SyllabusDTO syllabusDTO : requestyllabusDTOs) {
                TrainingProgramSyllabus loc = new TrainingProgramSyllabus();
                loc.setTrainingProgram(trainingProgramRepository.findOneById(trainingProgramId));
                loc.setSyllabus(syllabusRepository.findOneById(syllabusDTO.getId()));
                trainingProgramSyllabusRepository.save(loc);
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
    public ResponseEntity<?> searchSortFilter(TrainingProgramDTO trainingProgramDTO, int page, int limit) {
        String name = trainingProgramDTO.getName();
        Long startTime = trainingProgramDTO.getStartTime();
        Long duration = trainingProgramDTO.getDuration();
        Integer training_status = trainingProgramDTO.getTraining_status();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<TrainingProgram> entities = trainingProgramRepository.searchSortFilter(name, startTime, duration, training_status, pageable);
        List<TrainingProgramDTO> result = new ArrayList<>();
        Long count = trainingProgramRepository.countSearchSortFilter(name, startTime, duration, training_status);
        for (TrainingProgram entity : entities){
            TrainingProgramDTO newDTO = (TrainingProgramDTO) genericConverter.toDTO(entity, TrainingProgramDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                count);
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
        Long count = trainingProgramRepository.countSearchSortFilter(name, startTime, duration, training_status);
        for (TrainingProgram entity : entities){
            TrainingProgramDTO newDTO = (TrainingProgramDTO) genericConverter.toDTO(entity, TrainingProgramDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                count);
    }
}
