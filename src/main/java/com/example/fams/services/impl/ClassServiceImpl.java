package com.example.fams.services.impl;

import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.ContentDTO;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.UserDTO;
import com.example.fams.entities.Content;
import com.example.fams.entities.FamsClass;
import com.example.fams.entities.ClassUser;
import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.LearningObjectiveContent;
import com.example.fams.entities.User;
import com.example.fams.repository.ClassRepository;
import com.example.fams.repository.ClassUserRepository;
import com.example.fams.repository.UserClassRepository;
import com.example.fams.repository.UserRepository;
import com.example.fams.services.IClassService;
import com.example.fams.services.ServiceUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("ClassService")
public class ClassServiceImpl implements IClassService {

  private final ClassRepository classRepository;
  private final UserClassRepository userClassRepository;
  private final ClassUserRepository classUserRepository;
  private final UserRepository userRepository;
  private final GenericConverter genericConverter;

  public ClassServiceImpl(ClassRepository classRepository, UserClassRepository userClassRepository,
      ClassUserRepository classUserRepository, UserRepository userRepository, GenericConverter genericConverter) {
    this.classRepository = classRepository;
    this.userClassRepository = userClassRepository;
    this.classUserRepository = classUserRepository;
    this.userRepository = userRepository;
    this.genericConverter = genericConverter;
  }


  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<FamsClass> entities = classRepository.findAllByStatusIsTrue(pageable);
    List<ClassDTO> result = new ArrayList<>();

    convertListClassToListClassDTO(entities, result);

    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        classRepository.countAllByStatusIsTrue());

  }

  @Override
  public ResponseEntity<?> save(ClassDTO classDTO) {
    ServiceUtils.errors.clear();
    List<Long> requestUserIds = classDTO.getUserIds();
    FamsClass entity;

    // * Validate requestDTO ( if left null, then can be updated later )
    if (requestUserIds != null){
      ServiceUtils.validateUserIds(requestUserIds, userRepository);
    }
    if (!ServiceUtils.errors.isEmpty()) {
      throw new CustomValidationException(ServiceUtils.errors);
    }

    // * For update request
    if (classDTO.getId() != null){
      FamsClass oldEntity = classRepository.findById(classDTO.getId());
      FamsClass tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
      entity = convertDtoToEntity(classDTO);
      ServiceUtils.fillMissingAttribute(entity, tempOldEntity);
      classUserRepository.deleteAllByFamsClassId(classDTO.getId());
      loadClassUserFromListUserId(requestUserIds, entity.getId());
      entity.markModified();
      classRepository.save(entity);
    }

    // * For create request
    else {
      classDTO.setStatus(true);
      entity = (FamsClass) genericConverter.toEntity(classDTO, FamsClass.class);
      classRepository.save(entity);
      loadClassUserFromListUserId(requestUserIds, entity.getId());
    }


    ClassDTO result = (ClassDTO) genericConverter.toDTO(entity, ClassDTO.class);
    result.setUserIds(requestUserIds);
    return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
  }

  private void loadClassUserFromListUserId(List<Long> requestUserIds, Long classId) {
    if (requestUserIds != null && !requestUserIds.isEmpty()) {
      for (Long userId : requestUserIds) {
        ClassUser clu = new ClassUser();
        clu.setFamsClass(classRepository.findById(classId));
        clu.setUser(userRepository.findById(userId).get());
        classUserRepository.save(clu);
      }
    }
  }

  @Override
  public ResponseEntity<?> findById(Long id) {
    FamsClass entity = classRepository.findByStatusIsTrueAndId(id);
    if (entity != null) {
      ClassDTO result = (ClassDTO) genericConverter.toDTO(entity, ClassDTO.class);
      return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    } else {
      return ResponseUtil.error("Class not found", "Cannot Find Class", HttpStatus.NOT_FOUND);
    }

  }

  @Override
  public ResponseEntity<?> findAll(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<FamsClass> entities = classRepository.findAllBy(pageable);
    List<ClassDTO> result = new ArrayList<>();

    convertListClassToListClassDTO(entities, result);

    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        classRepository.countAllByStatusIsTrue());
  }

  @Override
  public ResponseEntity<?> changeStatus(Long id) {
    FamsClass entity = classRepository.findById(id);
    if (entity != null) {
      if (entity.getStatus()) {
        entity.setStatus(false);
      } else {
        entity.setStatus(true);
      }
      classRepository.save(entity);
      return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
    } else {
      return ResponseUtil.error("Class not found", "Cannot change status of non-existing Class", HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<?> searchSortFilter(ClassDTO classDTO, int page, int limit) {
    String code = classDTO.getCode();
    String name = classDTO.getName();
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<FamsClass> entities = classRepository.searchSortFilter(code, name, pageable);
    List<ClassDTO> result = new ArrayList<>();
    Long count = classRepository.countSearchSortFilter(code, name);
    convertListClassToListClassDTO(entities, result);
    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        count);
  }

  @Override
  public ResponseEntity<?> searchSortFilterADMIN(ClassDTO classDTO, String sortById, int page, int limit) {
    String code = classDTO.getCode();
    String name = classDTO.getName();
//    Long duration = classDTO.getDuration();
//    Long startDate = classDTO.getStartDate();
//    Long endDate = classDTO.getEndDate();
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<FamsClass> entities = classRepository.searchSortFilterADMIN(code, name, sortById,  pageable);
    List<ClassDTO> result = new ArrayList<>();
    Long count = classRepository.countSearchSortFilter(code, name);
    convertListClassToListClassDTO(entities, result);
    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        count);
  }

  private void convertListClassToListClassDTO(List<FamsClass> entities, List<ClassDTO> result) {
    for (FamsClass famsClass : entities){
      ClassDTO newClassDTO = (ClassDTO) genericConverter.toDTO(famsClass, ClassDTO.class);
      List<User> users = classUserRepository.findUserByClassId(famsClass.getId());
      if (famsClass.getClassUsers() == null){
        newClassDTO.setUserIds(null);
      }
      else {
        // ! Set list learningObjectiveIds và unitId sau khi convert ở trên vào contentDTO
        List<Long> userIds = users.stream()
            .map(User::getId)
            .toList();


        newClassDTO.setUserIds(userIds);

      }
      result.add(newClassDTO);
    }
  }

  public FamsClass convertDtoToEntity(ClassDTO classDTO) {
    FamsClass famsClass = new FamsClass();
    famsClass.setId(classDTO.getId());
    famsClass.setName(classDTO.getName());
    famsClass.setCode(classDTO.getCode());
    famsClass.setStatus(classDTO.getStatus());

    return famsClass;
  }


}
