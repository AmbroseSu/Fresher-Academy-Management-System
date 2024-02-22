package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.SyllabusDTO;
import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.Syllabus;
import com.example.fams.repository.LearningObjectiveRepository;
import com.example.fams.repository.SyllabusRepository;
import com.example.fams.services.IGenericService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("SyllabusService")
public class SyllabusServiceImpl implements IGenericService<SyllabusDTO> {

    private final SyllabusRepository syllabusRepository;
    private final GenericConverter genericConverter;

    public SyllabusServiceImpl(SyllabusRepository syllabusRepository, GenericConverter genericConverter) {
        this.syllabusRepository = syllabusRepository;
        this.genericConverter = genericConverter;
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Syllabus entity = syllabusRepository.findByStatusIsTrueAndId(id);
        SyllabusDTO result = (SyllabusDTO) genericConverter.toDTO(entity, SyllabusDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        List<Syllabus> entities = syllabusRepository.findByStatusIsTrue();
        List<SyllabusDTO> result = new ArrayList<>();
        for (Syllabus entity : entities) {
            SyllabusDTO newDTO = (SyllabusDTO) genericConverter.toDTO(entity, SyllabusDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                syllabusRepository.count());
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {
        List<Syllabus> entities = syllabusRepository.findAll();
        List<SyllabusDTO> result = new ArrayList<>();
        for (Syllabus entity : entities) {
            SyllabusDTO newDTO = (SyllabusDTO) genericConverter.toDTO(entity, SyllabusDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                syllabusRepository.count());
    }

    @Override
    public ResponseEntity<?> save(SyllabusDTO syllabusDTO) {
        Syllabus entity = new Syllabus();
        if (syllabusDTO.getName() != null){
            Optional<Syllabus> oldEntity = syllabusRepository.findById(syllabusDTO.getName());
            entity = (Syllabus) genericConverter.updateEntity(syllabusDTO, oldEntity);
        } else {
            entity = (Syllabus) genericConverter.toEntity(syllabusDTO, Syllabus.class);
        }

        syllabusRepository.save(entity);
        SyllabusDTO result = (SyllabusDTO) genericConverter.toDTO(entity, SyllabusDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }

    @Override
    public ResponseEntity<?> changeStatus(Long id) {
        Syllabus entity = syllabusRepository.findById(id);
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
    }

