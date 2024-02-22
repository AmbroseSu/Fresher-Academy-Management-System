package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ContentDTO;
import com.example.fams.entities.Content;
import com.example.fams.repository.ContentRepository;
import com.example.fams.services.IGenericService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("ContentService")
public class ContentServiceImpl implements IGenericService<ContentDTO> {


  private final ContentRepository contentRepository;
  private final GenericConverter genericConverter;

  public ContentServiceImpl(ContentRepository contentRepository, GenericConverter genericConverter) {
    this.contentRepository = contentRepository;
    this.genericConverter = genericConverter;
  }


  @Override
  public ResponseEntity<?> findById(Long id) {
    Content entity = contentRepository.findByStatusIsTrueAndId(id);
    ContentDTO result = (ContentDTO) genericConverter.toDTO(entity, ContentDTO.class);
    return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
  }

  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Content> entities = contentRepository.findByStatusIsTrue(pageable);
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
        contentRepository.count());
  }

  @Override
  public ResponseEntity<?> findAll(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Content> entities = contentRepository.findAll(pageable);
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
        contentRepository.count());
  }

  @Override
  public ResponseEntity<?> save(ContentDTO contentDTO) {
    Content entity = new Content();
    if (contentDTO.getId() != null){
      //Class oldEntity = classRepository.findById(classDTO.getId());
      Content oldEntity = contentRepository.findById(contentDTO.getId());
      entity = (Content) genericConverter.updateEntity(contentDTO, oldEntity);
    } else {
      entity = (Content) genericConverter.toEntity(contentDTO, Content.class);
    }

    contentRepository.save(entity);
    ContentDTO result = (ContentDTO) genericConverter.toDTO(entity, ContentDTO.class);
    return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
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
}
