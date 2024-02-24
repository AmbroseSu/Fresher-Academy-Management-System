package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ContentDTO;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.response.LearningObjectiveResponse;
import com.example.fams.entities.Content;
import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.LearningObjectiveContent;
import com.example.fams.repository.ContentRepository;
import com.example.fams.repository.LearningObjectiveContentRepository;
import com.example.fams.repository.LearningObjectiveRepository;
import com.example.fams.services.ILearningObjectiveService;
import com.example.fams.services.ServiceUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("LearningObjectiveService")
public class LearningObjectiveServiceImpl implements ILearningObjectiveService {

    private final LearningObjectiveRepository learningObjectiveRepository;
    private final LearningObjectiveContentRepository learningObjectiveContentRepository;
    private final ContentRepository contentRepository;
    private final GenericConverter genericConverter;

    public LearningObjectiveServiceImpl(LearningObjectiveContentRepository learningObjectiveContentRepository, ContentRepository contentRepository, LearningObjectiveRepository learningObjectiveRepository, GenericConverter genericConverter) {
        this.learningObjectiveRepository = learningObjectiveRepository;
        this.learningObjectiveContentRepository = learningObjectiveContentRepository;
        this.genericConverter = genericConverter;
        this.contentRepository = contentRepository;
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<LearningObjective> entities = learningObjectiveRepository.findAllByStatusIsTrue(pageable);
        List<LearningObjectiveDTO> result = new ArrayList<>();

        // ? Với mỗi learningObjective, chuyển nó thành learningObjectiveDTO (chưa có List<ContentDTO> ở trong)
        for (LearningObjective entity : entities) {
            LearningObjectiveDTO newLearningObjectiveDTO = (LearningObjectiveDTO) genericConverter.toDTO(entity, LearningObjectiveDTO.class);

            // * Lấy list Content từ learningObjectiveId
            List<Content> contents = learningObjectiveContentRepository.findContentsByLearningObjectiveId(entity.getId());

            // * Với mỗi content trong list content vừa lấy được, convert sang contentDTO rồi nhét vào list contentDTOS
            List<ContentDTO> contentDTOS = new ArrayList<>();
            for (Content content : contents) {
                ContentDTO newContentDTO = (ContentDTO) genericConverter.toDTO(content, ContentDTO.class);
                contentDTOS.add(newContentDTO);
            }

            // ! Set list contentDTO sau khi convert ở trên vào learningObjectiveDTO
            newLearningObjectiveDTO.setContentDTOs(contentDTOS);

            // todo trả về List DTO đã có contentDTOs ở trong
            result.add(newLearningObjectiveDTO);
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
        List<ContentDTO> requestContentDTOs = learningObjectiveDTO.getContentDTOs();

        LearningObjective entity;

        // * For update request
        if (learningObjectiveDTO.getId() != null){
            LearningObjective oldEntity = learningObjectiveRepository.findById(learningObjectiveDTO.getId());
            LearningObjective tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
            entity = (LearningObjective) genericConverter.updateEntity(learningObjectiveDTO, oldEntity);
            entity = ServiceUtils.fillMissingAttribute(entity, tempOldEntity);
            learningObjectiveContentRepository.deleteAllByLearningObjectiveId(learningObjectiveDTO.getId());
            loadLearningObjectiveContentFromListContentId(requestContentDTOs, entity.getId());
            learningObjectiveRepository.save(entity);
        }

        // * For create request
        else {
            learningObjectiveDTO.setStatus(true);
            entity = (LearningObjective) genericConverter.toEntity(learningObjectiveDTO, LearningObjective.class);
            learningObjectiveRepository.save(entity);
            loadLearningObjectiveContentFromListContentId(requestContentDTOs, entity.getId());
        }


        LearningObjectiveDTO result = (LearningObjectiveDTO) genericConverter.toDTO(entity, LearningObjectiveDTO.class);

        List<Content> contents = learningObjectiveContentRepository.findContentsByLearningObjectiveId(entity.getId());
        List<ContentDTO> contentDTOS = new ArrayList<>();
        for (Content content : contents) {
            ContentDTO newContentDTO = (ContentDTO) genericConverter.toDTO(content, ContentDTO.class);
            contentDTOS.add(newContentDTO);
        }
        result.setContentDTOs(contentDTOS);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }

    private void loadLearningObjectiveContentFromListContentId(List<ContentDTO> requestContentDTOs, Long learningObjectiveId) {
        if (requestContentDTOs != null && !requestContentDTOs.isEmpty()) {
            for (ContentDTO contentDTO : requestContentDTOs) {
                LearningObjectiveContent loc = new LearningObjectiveContent();
                loc.setLearningObjective(learningObjectiveRepository.findById(learningObjectiveId));
                loc.setContent(contentRepository.findById(contentDTO.getId()));
                learningObjectiveContentRepository.save(loc);
            }
        }
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
        for (LearningObjective entity : entities){
            LearningObjectiveDTO newDTO = (LearningObjectiveDTO) genericConverter.toDTO(entity, LearningObjectiveDTO.class);
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
    public ResponseEntity<?> searchSortFilterADMIN(LearningObjectiveDTO learningObjectiveDTO, String sortById, int page, int limit) {
        String code = learningObjectiveDTO.getCode();
        String name = learningObjectiveDTO.getName();
        Integer type = learningObjectiveDTO.getType();
        String description = learningObjectiveDTO.getDescription();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<LearningObjective> entities = learningObjectiveRepository.searchSortFilterADMIN(code, name, type, description, sortById, pageable);
        List<LearningObjectiveDTO> result = new ArrayList<>();
        Long count = learningObjectiveRepository.countSearchSortFilter(code, name, type, description);
        for (LearningObjective entity : entities){
            LearningObjectiveDTO newDTO = (LearningObjectiveDTO) genericConverter.toDTO(entity, LearningObjectiveDTO.class);
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
