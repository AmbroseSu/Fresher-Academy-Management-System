package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ContentDTO;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.entities.Content;
import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.LearningObjectiveContent;
import com.example.fams.repository.ContentLearningObjectiveRepository;
import com.example.fams.repository.ContentRepository;
import com.example.fams.repository.LearningObjectiveContentRepository;
import com.example.fams.repository.LearningObjectiveRepository;
import com.example.fams.services.IContentService;
import com.example.fams.services.IGenericService;
import com.example.fams.services.ServiceUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("ContentService")
public class ContentServiceImpl implements IContentService {


  private final ContentRepository contentRepository;
  private final ContentLearningObjectiveRepository contentLearningObjectiveRepository;
  private final LearningObjectiveRepository learningObjectiveRepository;
  private final GenericConverter genericConverter;

  public ContentServiceImpl(ContentRepository contentRepository,
      ContentLearningObjectiveRepository contentLearningObjectiveRepository,
      LearningObjectiveRepository learningObjectiveRepository, GenericConverter genericConverter) {
    this.contentRepository = contentRepository;
    this.contentLearningObjectiveRepository = contentLearningObjectiveRepository;
    this.learningObjectiveRepository = learningObjectiveRepository;
    this.genericConverter = genericConverter;
  }

  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Content> entities = contentRepository.findAllByStatusIsTrue(pageable);
    List<ContentDTO> result = new ArrayList<>();

    for (Content entity : entities) {
      ContentDTO newContentDTO = (ContentDTO) genericConverter.toDTO(entity, ContentDTO.class);

      List<LearningObjective> learningObjectives = contentLearningObjectiveRepository.findLearningObjectivesByContentId(entity.getId());

      List<LearningObjectiveDTO> learningObjectiveDTOS = new ArrayList<>();
      for (LearningObjective learningObjective : learningObjectives) {
        LearningObjectiveDTO newLearningObjectiveDTO = (LearningObjectiveDTO) genericConverter.toDTO(learningObjective, LearningObjectiveDTO.class);
        learningObjectiveDTOS.add(newLearningObjectiveDTO);
      }

      newContentDTO.setLearningObjectiveDTOS(learningObjectiveDTOS);

      result.add(newContentDTO);
    }


    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        contentRepository.countAllByStatusIsTrue());
  }

  @Override
  public ResponseEntity<?> save(ContentDTO contentDTO) {
    List<LearningObjectiveDTO> requestLearningObjectiveDTOs = contentDTO.getLearningObjectiveDTOS();

    Content entity;

    // * For update request
    if (contentDTO.getId() != null){
      Content oldEntity = contentRepository.findById(contentDTO.getId());
      Content tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
      entity = (Content) genericConverter.updateEntity(contentDTO, oldEntity);
      entity = ServiceUtils.fillMissingAttribute(entity, tempOldEntity);
      contentLearningObjectiveRepository.deleteAllByContentId(contentDTO.getId());
      loadContentLearningObjectiveFromListLearningObjectiveId(requestLearningObjectiveDTOs, entity.getId());
      entity.markModified();
      contentRepository.save(entity);
    }

    // * For create request
    else {
      contentDTO.setStatus(true);
      entity = (Content) genericConverter.toEntity(contentDTO, Content.class);
      contentRepository.save(entity);
      loadContentLearningObjectiveFromListLearningObjectiveId(requestLearningObjectiveDTOs, entity.getId());
    }


    ContentDTO result = (ContentDTO) genericConverter.toDTO(entity, ContentDTO.class);

    List<LearningObjective> learningObjectives = contentLearningObjectiveRepository.findLearningObjectivesByContentId(entity.getId());
    List<LearningObjectiveDTO> learningObjectiveDTOS = new ArrayList<>();
    for (LearningObjective learningObjective : learningObjectives) {
      LearningObjectiveDTO newLearningObjectiveDTO = (LearningObjectiveDTO) genericConverter.toDTO(learningObjective, LearningObjectiveDTO.class);
      learningObjectiveDTOS.add(newLearningObjectiveDTO);
    }
    result.setLearningObjectiveDTOS(learningObjectiveDTOS);
    return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
  }

  private void loadContentLearningObjectiveFromListLearningObjectiveId(List<LearningObjectiveDTO> requestLearningObjectiveDTOs, Long contentId) {
    if (requestLearningObjectiveDTOs != null && !requestLearningObjectiveDTOs.isEmpty()) {
      for (LearningObjectiveDTO learningObjectiveDTO : requestLearningObjectiveDTOs) {
        LearningObjectiveContent clo = new LearningObjectiveContent();
        clo.setContent(contentRepository.findById(contentId));
        clo.setLearningObjective(learningObjectiveRepository.findById(learningObjectiveDTO.getId()));
        contentLearningObjectiveRepository.save(clo);
      }
    }
  }

  @Override
  public ResponseEntity<?> findById(Long id) {
    Content entity = contentRepository.findByStatusIsTrueAndId(id);
    if (entity != null) {
      ContentDTO result = (ContentDTO) genericConverter.toDTO(entity, ContentDTO.class);
      return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    } else {
      return ResponseUtil.error("Content not found", "Cannot Find Content", HttpStatus.NOT_FOUND);
    }

  }

  @Override
  public ResponseEntity<?> findAll(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Content> entities = contentRepository.findAllByOrderByIdDesc(pageable);
    List<ContentDTO> result = new ArrayList<>();
    for (Content entity : entities) {
      ContentDTO newDTO = (ContentDTO) genericConverter.toDTO(entity, ContentDTO.class);
      result.add(newDTO);
    }
    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        contentRepository.countAllByStatusIsTrue());
  }

  @Override
  public ResponseEntity<?> changeStatus(Long id) {
    Content entity = contentRepository.findById(id);
    if (entity != null) {
      if (entity.getStatus()) {
        entity.setStatus(false);
      } else {
        entity.setStatus(true);
      }
      contentRepository.save(entity);
      return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
    } else {
      return ResponseUtil.error("LearningObjective not found", "Cannot change status of non-existing LearningObjective", HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<?> searchSortFilter(ContentDTO contentDTO, int page, int limit) {
    Integer delieryType = contentDTO.getDeliveryType();
    Long duration = contentDTO.getDuration();
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Content> entities = contentRepository.searchSortFilter(delieryType, duration, pageable);
    List<ContentDTO> result = new ArrayList<>();
    Long count = contentRepository.countSearchSortFilter(delieryType, duration);
    for (Content entity : entities){
      ContentDTO newDTO = (ContentDTO) genericConverter.toDTO(entity, ContentDTO.class);
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
  public ResponseEntity<?> searchSortFilterADMIN(ContentDTO contentDTO, String sortById, int page, int limit) {
    Integer deliveryType = contentDTO.getDeliveryType();
    Long duration = contentDTO.getDuration();
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Content> entities = contentRepository.searchSortFilterADMIN(deliveryType, duration, sortById, pageable);
    List<ContentDTO> result = new ArrayList<>();
    Long count = contentRepository.countSearchSortFilter(deliveryType, duration);
    for (Content entity : entities){
      ContentDTO newDTO = (ContentDTO) genericConverter.toDTO(entity, ContentDTO.class);
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
