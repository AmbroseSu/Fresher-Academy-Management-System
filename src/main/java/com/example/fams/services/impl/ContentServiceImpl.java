package com.example.fams.services.impl;

import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ContentDTO;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.UnitDTO;
import com.example.fams.entities.*;
import com.example.fams.repository.*;
import com.example.fams.services.IContentService;
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
  private final UnitRepository unitRepository;
  private final LearningObjectiveContentRepository learningObjectiveContentRepository;
  private final ContentLearningObjectiveRepository contentLearningObjectiveRepository;
  private final LearningObjectiveRepository learningObjectiveRepository;
  private final GenericConverter genericConverter;

  public ContentServiceImpl(ContentRepository contentRepository, UnitRepository unitRepository, LearningObjectiveContentRepository learningObjectiveContentRepository,
                            ContentLearningObjectiveRepository contentLearningObjectiveRepository,
                            LearningObjectiveRepository learningObjectiveRepository, GenericConverter genericConverter) {
    this.contentRepository = contentRepository;
      this.unitRepository = unitRepository;
      this.learningObjectiveContentRepository = learningObjectiveContentRepository;
      this.contentLearningObjectiveRepository = contentLearningObjectiveRepository;
    this.learningObjectiveRepository = learningObjectiveRepository;
    this.genericConverter = genericConverter;
  }

  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Content> entities = contentRepository.findAllByStatusIsTrue(pageable);
    List<ContentDTO> result = new ArrayList<>();

    convertListContentToListContentDTO(entities, result);

    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        contentRepository.countAllByStatusIsTrue());
  }

  @Override
  public ResponseEntity<?> save(ContentDTO contentDTO) {
    ServiceUtils.errors.clear();
    List<Long> requestLearningObjectiveIds = contentDTO.getLearningObjectiveIds();
    Long requestUnitId = contentDTO.getUnitId();
    Content entity;

    // * Validate requestDTO ( if left null, then can be updated later )
    if (requestLearningObjectiveIds != null){
      ServiceUtils.validateLearningObjectiveIds(requestLearningObjectiveIds, learningObjectiveRepository);
    }
    if (requestUnitId != null){
      ServiceUtils.validateUnitIds(List.of(requestUnitId), unitRepository);
    }
    if (!ServiceUtils.errors.isEmpty()) {
      throw new CustomValidationException(ServiceUtils.errors);
    }

    // * For update request
    if (contentDTO.getId() != null){
      Content oldEntity = contentRepository.findById(contentDTO.getId());
      Content tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
      entity = convertDtoToEntity(contentDTO, unitRepository);
      entity = ServiceUtils.fillMissingAttribute(entity, tempOldEntity);
      contentLearningObjectiveRepository.deleteAllByContentId(contentDTO.getId());
      loadContentLearningObjectiveFromListLearningObjectiveId(requestLearningObjectiveIds, entity.getId());
      entity.setUnit(unitRepository.findById(requestUnitId));
      entity.markModified();
      contentRepository.save(entity);
    }

    // * For create request
    else {
      contentDTO.setStatus(true);
      entity = (Content) genericConverter.toEntity(contentDTO, Content.class);
      contentRepository.save(entity);
      loadContentLearningObjectiveFromListLearningObjectiveId(requestLearningObjectiveIds, entity.getId());
    }


    ContentDTO result = (ContentDTO) genericConverter.toDTO(entity, ContentDTO.class);
    result.setUnitId(requestUnitId);
    result.setLearningObjectiveIds(requestLearningObjectiveIds);
    return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
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
    List<Content> entities = contentRepository.findAllBy(pageable);
    List<ContentDTO> result = new ArrayList<>();

    convertListContentToListContentDTO(entities, result);

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
    if (entity != null){
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
    convertListContentToListContentDTO(entities, result);
    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        count);
  }

  @Override
  public ResponseEntity<?> searchSortFilterADMIN(ContentDTO contentDTO, String sortById, int page, int limit) {
    Integer delieryType = contentDTO.getDeliveryType();
    Long duration = contentDTO.getDuration();
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Content> entities = contentRepository.searchSortFilterADMIN(delieryType, duration, sortById,  pageable);
    List<ContentDTO> result = new ArrayList<>();
    Long count = contentRepository.countSearchSortFilter(delieryType, duration);
    convertListContentToListContentDTO(entities, result);
    return ResponseUtil.getCollection(result,
            HttpStatus.OK,
            "Fetched successfully",
            page,
            limit,
            count);
  }

  private void convertListContentToListContentDTO(List<Content> entities, List<ContentDTO> result) {
    for (Content content : entities){
      ContentDTO newContentDTO = (ContentDTO) genericConverter.toDTO(content, ContentDTO.class);
      List<LearningObjective> learningObjectives = contentLearningObjectiveRepository.findLearningObjectivesByContentId(content.getId());

      if (learningObjectives == null) newContentDTO.setLearningObjectiveIds(null);
      else {
        // ! Set list learningObjectiveIds và unitId sau khi convert ở trên vào contentDTO
        List<Long> learningObjectiveIds = learningObjectives.stream()
                .map(LearningObjective::getId)
                .toList();
        newContentDTO.setLearningObjectiveIds(learningObjectiveIds);
      }

      if (content.getUnit() == null) newContentDTO.setUnitId(null);
      else {
        Unit unit = contentRepository.findUnitByUnitId(content.getUnit().getId());
        newContentDTO.setUnitId(unit.getId());
      }
      result.add(newContentDTO);
    }
  }

  public Content convertDtoToEntity(ContentDTO contentDTO, UnitRepository unitRepository) {
    Content content = new Content();
    content.setId(contentDTO.getId());
    content.setDeliveryType(contentDTO.getDeliveryType());
    content.setDuration(contentDTO.getDuration());
    content.setStatus(contentDTO.getStatus());

    // Fetch the Unit using the provided unitId
    Unit unit = unitRepository.findById(contentDTO.getUnitId());
    content.setUnit(unit);

    return content;
  }

  private void loadContentLearningObjectiveFromListLearningObjectiveId(List<Long> requestLearningObjectiveIds, Long contentId) {
    if (requestLearningObjectiveIds != null && !requestLearningObjectiveIds.isEmpty()) {
      for (Long learningObjectiveId : requestLearningObjectiveIds) {
        LearningObjectiveContent clo = new LearningObjectiveContent();
        clo.setContent(contentRepository.findById(contentId));
        clo.setLearningObjective(learningObjectiveRepository.findById(learningObjectiveId));
        contentLearningObjectiveRepository.save(clo);
      }
    }
  }
}
