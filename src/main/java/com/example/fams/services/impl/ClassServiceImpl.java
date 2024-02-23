package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ClassDTO;
import com.example.fams.entities.Class;
import com.example.fams.repository.ClassRepository;
import com.example.fams.services.IGenericService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("ClassService")
public class ClassServiceImpl implements IGenericService<ClassDTO> {

  private final ClassRepository classRepository;
  private final GenericConverter genericConverter;

  public ClassServiceImpl(ClassRepository classRepository, GenericConverter genericConverter) {
    this.classRepository = classRepository;
    this.genericConverter = genericConverter;
  }

  @Override
  public ResponseEntity<?> findById(Long id) {
    Class entity = classRepository.findByStatusIsTrueAndId(id);
    ClassDTO result = (ClassDTO) genericConverter.toDTO(entity, ClassDTO.class);
    return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
  }

  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Class> entities = classRepository.findByStatusIsTrue(pageable);
    List<ClassDTO> result = new ArrayList<>();
    for (Class entity : entities) {
      ClassDTO newDTO = (ClassDTO) genericConverter.toDTO(entity, ClassDTO.class);
      result.add(newDTO);
    }
    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        classRepository.count());
  }

  @Override
  public ResponseEntity<?> findAll(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Class> entities = classRepository.findAll(pageable);
    List<ClassDTO> result = new ArrayList<>();
    for (Class entity : entities) {
      ClassDTO newDTO = (ClassDTO) genericConverter.toDTO(entity, ClassDTO.class);
      result.add(newDTO);
    }
    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        classRepository.count());
  }

  @Override
  public ResponseEntity<?> save(ClassDTO classDTO) {
    Class entity = new Class();

    if(classDTO.getStartDate() < classDTO.getEndDate()){
      if (classDTO.getId() != null){
        //Class oldEntity = classRepository.findById(classDTO.getId());
        Class oldEntity = classRepository.findById(classDTO.getId());
        entity = (Class) genericConverter.updateEntity(classDTO, oldEntity);
      } else {
        entity = (Class) genericConverter.toEntity(classDTO, Class.class);
      }

      classRepository.save(entity);
      ClassDTO result = (ClassDTO) genericConverter.toDTO(entity, ClassDTO.class);
      return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }else{
      return ResponseUtil.getObject(null, HttpStatus.OK, "Saved false by StartDate > EndDate");
    }


  }

  @Override
  public ResponseEntity<?> changeStatus(Long id) {
    Class entity = classRepository.findById(id);
    if (entity != null) {
      if (entity.getStatus()) {
        entity.setStatus(false);
      } else {
        entity.setStatus(true);
      }
      classRepository.save(entity);
      return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
    } else {
      return ResponseUtil.error("LearningObjective not found", "Cannot change status of non-existing LearningObjective", HttpStatus.NOT_FOUND);
    }
  }
}
