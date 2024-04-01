package com.example.fams.services.impl;

import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ContentDTO;
import com.example.fams.entities.*;
import com.example.fams.entities.enums.DeliveryType;
import com.example.fams.repository.*;
import com.example.fams.services.IContentService;
import com.example.fams.services.ServiceUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service("ContentService")
public class ContentServiceImpl implements IContentService {


  private final ContentRepository contentRepository;
  private final UnitRepository unitRepository;
  private final LearningObjectiveContentRepository learningObjectiveContentRepository;
  private final ContentLearningObjectiveRepository contentLearningObjectiveRepository;
  private final LearningObjectiveRepository learningObjectiveRepository;
  private final OutputStandardRepository outputStandardRepository;
  private final GenericConverter genericConverter;

  public ContentServiceImpl(ContentRepository contentRepository, UnitRepository unitRepository, LearningObjectiveContentRepository learningObjectiveContentRepository,
                            ContentLearningObjectiveRepository contentLearningObjectiveRepository,
                            LearningObjectiveRepository learningObjectiveRepository, OutputStandardRepository outputStandardRepository, GenericConverter genericConverter) {
    this.contentRepository = contentRepository;
      this.unitRepository = unitRepository;
      this.learningObjectiveContentRepository = learningObjectiveContentRepository;
      this.contentLearningObjectiveRepository = contentLearningObjectiveRepository;
    this.learningObjectiveRepository = learningObjectiveRepository;
    this.outputStandardRepository = outputStandardRepository;
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
    List<Long> requestOutputStandardIds = contentDTO.getOutputStandardIds();
    Long requestUnitId = contentDTO.getUnitId();
    Content entity;

    // * Validate requestDTO ( if left null, then can be updated later )
    if (requestLearningObjectiveIds != null){
      ServiceUtils.validateLearningObjectiveIds(requestLearningObjectiveIds, learningObjectiveRepository);
    }
    if (requestOutputStandardIds != null) {
      ServiceUtils.validateOutputStandardIds(requestOutputStandardIds, outputStandardRepository);
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
      entity = convertDtoToEntity(contentDTO, unitRepository);
      contentRepository.save(entity);
      loadContentLearningObjectiveFromListLearningObjectiveId(requestLearningObjectiveIds, entity.getId());
    }

    ContentDTO result = convertContentToContentDTO(entity);
    if (contentDTO.getId() == null){
      result.setLearningObjectiveIds(requestLearningObjectiveIds);
    }
    return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
  }

  @Override
  public ResponseEntity<?> findById(Long id) {
    Content entity = contentRepository.findByStatusIsTrueAndId(id);
    if (entity != null) {
      ContentDTO result = convertContentToContentDTO(entity);
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
  public Boolean checkExist(Long id) {
    Content content = contentRepository.findById(id);
    return content != null;

  }

  @Override
  public ResponseEntity<?> searchSortFilter(ContentDTO contentDTO, int page, int limit) {
    DeliveryType deliveryType = contentDTO.getDeliveryType();
    Long duration = contentDTO.getDuration();
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Content> entities = contentRepository.searchSortFilter(deliveryType, duration, pageable);
    List<ContentDTO> result = new ArrayList<>();
    Long count = contentRepository.countSearchSortFilter(deliveryType, duration);
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
    DeliveryType deliveryType = contentDTO.getDeliveryType();
    Long duration = contentDTO.getDuration();
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Content> entities = contentRepository.searchSortFilterADMIN(deliveryType, duration, sortById,  pageable);
    List<ContentDTO> result = new ArrayList<>();
    Long count = contentRepository.countSearchSortFilter(deliveryType, duration);
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
      ContentDTO newContentDTO = convertContentToContentDTO(content);
      result.add(newContentDTO);
    }
  }

  private ContentDTO convertContentToContentDTO(Content entity) {
    ContentDTO newContentDTO = (ContentDTO) genericConverter.toDTO(entity, ContentDTO.class);
    List<LearningObjective> learningObjectives = contentLearningObjectiveRepository.findLearningObjectivesByContentId(entity.getId());
    List<OutputStandard> outputStandards = outputStandardRepository.findByContent_Id(entity.getId());

    if (learningObjectives == null) newContentDTO.setLearningObjectiveIds(null);
    else {
      // ! Set list learningObjectiveIds và unitId sau khi convert ở trên vào contentDTO
      List<Long> learningObjectiveIds = learningObjectives.stream()
              .map(LearningObjective::getId)
              .toList();
      newContentDTO.setLearningObjectiveIds(learningObjectiveIds);
    }
    if (outputStandards == null) newContentDTO.setOutputStandardIds(null);
    else {
      List<Long> outputStandardIds = outputStandards.stream()
              .map(OutputStandard::getId)
              .toList();
        newContentDTO.setOutputStandardIds(outputStandardIds);
    }

    if (entity.getUnit() == null) newContentDTO.setUnitId(null);
    else {
      Unit unit = contentRepository.findUnitByUnitId(entity.getUnit().getId());
      newContentDTO.setUnitId(unit.getId());
    }
    return newContentDTO;
  }

  public Content convertDtoToEntity(ContentDTO contentDTO, UnitRepository unitRepository) {
    Content content = new Content();
    content.setId(contentDTO.getId());
    content.setDeliveryType(contentDTO.getDeliveryType());
    content.setDuration(contentDTO.getDuration());
    content.setStatus(contentDTO.getStatus());
    content.setTrainingFormat(contentDTO.getTrainingFormat());
    content.setDeliveryType(contentDTO.getDeliveryType());
    if (contentDTO.getOutputStandardIds() != null) {
      List<OutputStandard> outputStandards = contentDTO.getOutputStandardIds().stream()
              .map(outputStandardRepository::findById)
              .filter(Objects::nonNull)
              .peek(outputStandard -> outputStandard.setContent(content))
              .toList();
      content.setOutputStandards(outputStandards);
    }

    // Fetch the Unit using the provided unitId
    Unit unit = unitRepository.findById(contentDTO.getUnitId());
    content.setUnit(unit);

    return content;
  }

  private void loadContentLearningObjectiveFromListLearningObjectiveId(List<Long> requestLearningObjectiveIds, Long contentId) {
    if (requestLearningObjectiveIds != null && !requestLearningObjectiveIds.isEmpty()) {
      for (Long learningObjectiveId : requestLearningObjectiveIds) {
        LearningObjective learningObjective = learningObjectiveRepository.findById(learningObjectiveId);
        Content content = contentRepository.findById(contentId);
        if (learningObjective != null && content != null) {
          LearningObjectiveContent clo = new LearningObjectiveContent();
          clo.setContent(content);
          clo.setLearningObjective(learningObjective);
          learningObjectiveContentRepository.save(clo);
        }
      }
    }
  }
}
