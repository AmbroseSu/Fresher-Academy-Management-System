package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.entities.TrainingProgram;
import com.example.fams.repository.TrainingProgramRepository;
import com.example.fams.services.IGenericService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("TrainingProgramService")
public class TrainingProgramServiceIpl implements IGenericService<TrainingProgramDTO> {

    private final TrainingProgramRepository trainingProgramRepository;
    private final GenericConverter genericConverter;

    public TrainingProgramServiceIpl(TrainingProgramRepository trainingProgramRepository, GenericConverter genericConverter) {
        this.trainingProgramRepository = trainingProgramRepository;
        this.genericConverter = genericConverter;
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        TrainingProgram entity = trainingProgramRepository.findByStatusIsTrueAndId(id);
        TrainingProgramDTO result = (TrainingProgramDTO) genericConverter.toDTO(entity, TrainingProgramDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        List<TrainingProgram> entities = trainingProgramRepository.findByStatusIsTrue();
        List<TrainingProgramDTO> result = new ArrayList<>();
        for (TrainingProgram entity : entities) {
            TrainingProgramDTO newDTO = (TrainingProgramDTO) genericConverter.toDTO(entity, TrainingProgramDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                trainingProgramRepository.count());
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {
        List<TrainingProgram> entities = trainingProgramRepository.findAll();
        List<TrainingProgramDTO> result = new ArrayList<>();
        for (TrainingProgram entity : entities) {
            TrainingProgramDTO newDTO = (TrainingProgramDTO) genericConverter.toDTO(entity, TrainingProgramDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                trainingProgramRepository.count());
    }

    @Override
    public ResponseEntity<?> save(TrainingProgramDTO trainingProgramDTO) {
        TrainingProgram entity = new TrainingProgram();
        if (trainingProgramDTO.getName() != null){
            Optional<TrainingProgram> oldEntity = trainingProgramRepository.findByName(trainingProgramDTO.getName());
            if (oldEntity.isPresent()) {
                entity = (TrainingProgram) genericConverter.updateEntity(trainingProgramDTO, oldEntity.get());
            } else {
                entity = (TrainingProgram) genericConverter.toEntity(trainingProgramDTO, TrainingProgram.class);
            }
        }

        trainingProgramRepository.save(entity);
        TrainingProgramDTO result = (TrainingProgramDTO) genericConverter.toDTO(entity, TrainingProgramDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }

        @Override
        public ResponseEntity<?> changeStatus (Long id){
            TrainingProgram entity = trainingProgramRepository.findById(id);
            if (entity != null) {
                if (entity.getStatus()) {
                    entity.setStatus(false);
                } else {
                    entity.setStatus(true);
                }
                trainingProgramRepository.save(entity);
                return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
            } else {
                return ResponseUtil.error("TrainingProgram not found", "Cannot change status of non-existing TrainingProgram", HttpStatus.NOT_FOUND);
            }
        }
    }
