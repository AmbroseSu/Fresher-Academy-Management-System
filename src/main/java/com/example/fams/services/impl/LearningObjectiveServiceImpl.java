package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.Syllabus;
import com.example.fams.repository.LearningObjectiveRepository;
import com.example.fams.services.IGenericService;
import com.example.fams.services.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service("LearningObjectiveService")
public class LearningObjectiveServiceImpl implements IGenericService<LearningObjectiveDTO> {

    private final LearningObjectiveRepository learningObjectiveRepository;
    private final GenericConverter genericConverter;

    public LearningObjectiveServiceImpl(LearningObjectiveRepository learningObjectiveRepository, GenericConverter genericConverter) {
        this.learningObjectiveRepository = learningObjectiveRepository;
        this.genericConverter = genericConverter;
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<LearningObjective> entities = learningObjectiveRepository.findAllByStatusIsTrue(pageable);
        List<LearningObjectiveDTO> result = new ArrayList<>();
        for (LearningObjective entity : entities) {
            LearningObjectiveDTO newDTO = (LearningObjectiveDTO) genericConverter.toDTO(entity, LearningObjectiveDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                learningObjectiveRepository.countAllByStatusIsTrue());
    }

    @Override
    public ResponseEntity<?> save(LearningObjectiveDTO learningObjectiveDTO) {
        LearningObjective entity;
        if (learningObjectiveDTO.getId() != null){
            LearningObjective oldEntity = learningObjectiveRepository.findById(learningObjectiveDTO.getId());
            LearningObjective tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
            entity = (LearningObjective) genericConverter.updateEntity(learningObjectiveDTO, oldEntity);
            entity = ServiceUtils.fillMissingAttribute(entity, tempOldEntity);

        } else {
            learningObjectiveDTO.setStatus(true);
            entity = (LearningObjective) genericConverter.toEntity(learningObjectiveDTO, LearningObjective.class);
        }

        learningObjectiveRepository.save(entity);
        LearningObjectiveDTO result = (LearningObjectiveDTO) genericConverter.toDTO(entity, LearningObjectiveDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        LearningObjective entity = learningObjectiveRepository.findByStatusIsTrueAndId(id);
        LearningObjectiveDTO result = (LearningObjectiveDTO) genericConverter.toDTO(entity, LearningObjectiveDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<LearningObjective> entities = learningObjectiveRepository.findAllByOrderByIdDesc(pageable);
        List<LearningObjectiveDTO> result = new ArrayList<>();
        for (LearningObjective entity : entities) {
            LearningObjectiveDTO newDTO = (LearningObjectiveDTO) genericConverter.toDTO(entity, LearningObjectiveDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                learningObjectiveRepository.countAllByStatusIsTrue());
    }

    @Override
    public ResponseEntity<?> changeStatus(Long id) {
        LearningObjective entity = learningObjectiveRepository.findById(id);
        if (entity != null) {
            if (entity.getStatus()) {
                entity.setStatus(false);
            } else {
                entity.setStatus(true);
            }
            learningObjectiveRepository.save(entity);
            return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
        } else {
            return ResponseUtil.error("LearningObjective not found", "Cannot change status of non-existing LearningObjective", HttpStatus.NOT_FOUND);
        }
    }


}
