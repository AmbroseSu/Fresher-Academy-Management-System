package com.example.fams.services.impl;

import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ContentDTO;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.UserDTO;
import com.example.fams.dto.response.LearningObjectiveResponse;
import com.example.fams.entities.*;
import com.example.fams.repository.*;
import com.example.fams.services.ILearningObjectiveService;
import com.example.fams.services.ServiceUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("LearningObjectiveService")
public class LearningObjectiveServiceImpl implements ILearningObjectiveService {

    private final LearningObjectiveRepository learningObjectiveRepository;
    private final LearningObjectiveContentRepository learningObjectiveContentRepository;
    private final SyllabusObjectiveRepository syllabusObjectiveRepository;
    private final ContentRepository contentRepository;
    private final SyllabusRepository syllabusRepository;
    private final GenericConverter genericConverter;

    public LearningObjectiveServiceImpl(LearningObjectiveContentRepository learningObjectiveContentRepository, ContentRepository contentRepository, LearningObjectiveRepository learningObjectiveRepository, SyllabusObjectiveRepository syllabusObjectiveRepository, SyllabusRepository syllabusRepository, GenericConverter genericConverter) {
        this.learningObjectiveRepository = learningObjectiveRepository;
        this.learningObjectiveContentRepository = learningObjectiveContentRepository;
        this.syllabusObjectiveRepository = syllabusObjectiveRepository;
        this.syllabusRepository = syllabusRepository;
        this.genericConverter = genericConverter;
        this.contentRepository = contentRepository;
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<LearningObjective> entities = learningObjectiveRepository.findAllByStatusIsTrue(pageable);
        List<LearningObjectiveDTO> result = new ArrayList<>();

        convertListLoToListLoDTO(entities, result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                learningObjectiveRepository.countAllByStatusIsTrue());
    }

    @Override
    public ResponseEntity<?> save(LearningObjectiveDTO learningObjectiveDTO) {
        ServiceUtils.errors.clear();
        LearningObjective lo;
        List<Long> requestContentIds = learningObjectiveDTO.getContentIds();
        List<Long> requestSyllabusIds = learningObjectiveDTO.getSyllabusIds();

        // * Validate requestDTO ( if left null, then can be updated later )
        if (requestContentIds != null){
            ServiceUtils.validateContentIds(requestContentIds, contentRepository);
        }
        if (requestSyllabusIds != null){
            ServiceUtils.validateSyllabusIds(requestSyllabusIds, syllabusRepository);
        }
        if (!ServiceUtils.errors.isEmpty()) {
            throw new CustomValidationException(ServiceUtils.errors);
        }

        // * For update request
        if (learningObjectiveDTO.getId() != null){
            LearningObjective oldEntity = learningObjectiveRepository.findById(learningObjectiveDTO.getId());
            if (oldEntity != null) {
                LearningObjective tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
                lo = convertDtoToEntity(learningObjectiveDTO);
                ServiceUtils.fillMissingAttribute(lo, tempOldEntity);
                learningObjectiveContentRepository.deleteAllByLearningObjectiveId(learningObjectiveDTO.getId());
                loadContentLearningObjectiveFromListContentId(requestContentIds, lo.getId());
                syllabusObjectiveRepository.deleteAllByLearningObjectiveId(learningObjectiveDTO.getId());
                loadSyllabusObjectiveFromListSyllabusId(requestSyllabusIds, lo.getId());
                lo.markModified();
                learningObjectiveRepository.save(lo);
            } else {
                throw new RuntimeException("LearningObjective with id " + learningObjectiveDTO.getId() + " does not exists!");
            }
        }

        // * For create request
        else {
            learningObjectiveDTO.setStatus(true);
            lo = (LearningObjective) genericConverter.toEntity(learningObjectiveDTO, LearningObjective.class);
            learningObjectiveRepository.save(lo);
            loadContentLearningObjectiveFromListContentId(requestContentIds, lo.getId());
            loadSyllabusObjectiveFromListSyllabusId(requestSyllabusIds, lo.getId());
        }

        LearningObjectiveDTO result = convertLoToLoDTO(lo);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        LearningObjective entity = learningObjectiveRepository.findByStatusIsTrueAndId(id);
        LearningObjectiveDTO result = convertLoToLoDTO(entity);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<LearningObjective> entities = learningObjectiveRepository.findAllBy(pageable);
        List<LearningObjectiveDTO> result = new ArrayList<>();

        convertListLoToListLoDTO(entities, result);
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

    @Override
    public ResponseEntity<?> searchSortFilter(LearningObjectiveDTO learningObjectiveDTO, int page, int limit) {
        String code = learningObjectiveDTO.getCode();
        String name = learningObjectiveDTO.getName();
        Integer type = learningObjectiveDTO.getType();
        String description = learningObjectiveDTO.getDescription();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<LearningObjective> entities = learningObjectiveRepository.searchSortFilter(code, name, type, description, pageable);
        List<LearningObjectiveDTO> result = new ArrayList<>();
        Long count = learningObjectiveRepository.countSearchSortFilter(code, name, type, description);
        convertListLoToListLoDTO(entities,result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                count);
    }

    @Override
    public ResponseEntity<?> searchSortFilterADMIN(LearningObjectiveDTO learningObjectiveDTO, String sortById, int page, int limit) {
        String code = learningObjectiveDTO.getCode();
        String name = learningObjectiveDTO.getName();
        Integer type = learningObjectiveDTO.getType();
        String description = learningObjectiveDTO.getDescription();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<LearningObjective> entities = learningObjectiveRepository.searchSortFilterADMIN(code, name, type, description, sortById, pageable);
        List<LearningObjectiveDTO> result = new ArrayList<>();
        Long count = learningObjectiveRepository.countSearchSortFilter(code, name, type, description);
        convertListLoToListLoDTO(entities,result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                count);
    }

    private void convertListLoToListLoDTO(List<LearningObjective> entities, List<LearningObjectiveDTO> result) {
        for (LearningObjective lo : entities){
            result.add(convertLoToLoDTO(lo));
        }
    }

    private LearningObjectiveDTO convertLoToLoDTO(LearningObjective entity) {
        LearningObjectiveDTO newLoDTO = (LearningObjectiveDTO) genericConverter.toDTO(entity, LearningObjectiveDTO.class);
        List<Content> contents = learningObjectiveContentRepository.findContentsByLearningObjectiveId(entity.getId());
        List<Syllabus> syllabus = syllabusObjectiveRepository.findSyllabusByLearningObjectiveId(entity.getId());

        // ! Set list learningObjectiveIds và unitId sau khi convert ở trên vào contentDTO

        if (contents == null) newLoDTO.setContentIds(null);
        else {
            List<Long> contentIds = contents.stream()
                    .map(Content::getId)
                    .toList();
            newLoDTO.setContentIds(contentIds);
        }

        if (syllabus == null) newLoDTO.setSyllabusIds(null);
        else {
            List<Long> syllabusIds = syllabus.stream()
                    .map(Syllabus::getId)
                    .toList();
            newLoDTO.setSyllabusIds(syllabusIds);
        }
        return newLoDTO;
    }

    public LearningObjective convertDtoToEntity(LearningObjectiveDTO LoDTO) {
        LearningObjective learningObjective = new LearningObjective();
        learningObjective.setId(LoDTO.getId());
        learningObjective.setCode(LoDTO.getCode());
        learningObjective.setName(LoDTO.getName());
        learningObjective.setType(LoDTO.getType());
        learningObjective.setDescription(LoDTO.getDescription());
        learningObjective.setStatus(LoDTO.getStatus());

        return learningObjective;
    }

    private void loadContentLearningObjectiveFromListContentId(List<Long> requestContentIds, Long learningObjectiveId) {
        if (requestContentIds != null && !requestContentIds.isEmpty()) {
            for (Long contentId : requestContentIds) {
                Content content = contentRepository.findById(contentId);
                LearningObjective learningObjective = learningObjectiveRepository.findById(learningObjectiveId);
                if (content != null && learningObjective != null) {
                    LearningObjectiveContent clo = new LearningObjectiveContent();
                    clo.setContent(content);
                    clo.setLearningObjective(learningObjective);
                    learningObjectiveContentRepository.save(clo);
                }
            }
        }
    }

    private void loadSyllabusObjectiveFromListSyllabusId(List<Long> requestSyllabusIds, Long learningObjectiveId) {
        if (requestSyllabusIds != null && !requestSyllabusIds.isEmpty()) {
            for (Long syllabusId : requestSyllabusIds) {
                Syllabus syllabus = syllabusRepository.findById(syllabusId).get();
                LearningObjective learningObjective = learningObjectiveRepository.findById(learningObjectiveId);
                if (syllabus != null && learningObjective != null) {
                    SyllabusObjective so = new SyllabusObjective();
                    so.setSyllabus(syllabus);
                    so.setLearningObjective(learningObjective);
                    syllabusObjectiveRepository.save(so);
                }
            }
        }
    }

}
