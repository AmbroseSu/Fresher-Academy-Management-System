package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.UserDTO;
import com.example.fams.entities.FamsClass;
import com.example.fams.entities.ClassUser;
import com.example.fams.entities.User;
import com.example.fams.repository.ClassRepository;
import com.example.fams.repository.ClassUserRepository;
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
  private final ClassUserRepository classUserRepository;
  private final UserRepository userRepository;
  private final GenericConverter genericConverter;

  public ClassServiceImpl(ClassRepository classRepository, ClassUserRepository classUserRepository,
      UserRepository userRepository, GenericConverter genericConverter) {
    this.classRepository = classRepository;
    this.classUserRepository = classUserRepository;
    this.userRepository = userRepository;
    this.genericConverter = genericConverter;
  }


  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<FamsClass> entities = classRepository.findAllByStatusIsTrue(pageable);
    List<ClassDTO> result = new ArrayList<>();


    for (FamsClass entity : entities) {
      ClassDTO newClassDTO = (ClassDTO) genericConverter.toDTO(entity, ClassDTO.class);


      List<User> users = classUserRepository.findUserByClassId(entity.getId());

      List<UserDTO> userDTOs = new ArrayList<>();
      for (User user : users) {
        UserDTO newUserDTO = (UserDTO) genericConverter.toDTO(user, UserDTO.class);
        userDTOs.add(newUserDTO);
      }

      newClassDTO.setUserDTOs(userDTOs);

      result.add(newClassDTO);
    }


    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        classRepository.countAllByStatusIsTrue());
  }

  @Override
  public ResponseEntity<?> save(ClassDTO classDTO) {
    List<UserDTO> requestUserDTOs = classDTO.getUserDTOs();

    FamsClass entity;

    // * For update request
    if (classDTO.getId() != null){
      FamsClass oldEntity = classRepository.findById(classDTO.getId());
      FamsClass tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
      entity = (FamsClass) genericConverter.updateEntity(classDTO, oldEntity);
      entity = ServiceUtils.fillMissingAttribute(entity, tempOldEntity);
      classUserRepository.deleteAllByFamsClassId(classDTO.getId());
      loadClassUserFromListUserId(requestUserDTOs, entity.getId());
      entity.markModified();
      classRepository.save(entity);
    }

    // * For create request
    else {
      classDTO.setStatus(true);
      entity = (FamsClass) genericConverter.toEntity(classDTO, FamsClass.class);
      classRepository.save(entity);
      loadClassUserFromListUserId(requestUserDTOs, entity.getId());
    }


    ClassDTO result = (ClassDTO) genericConverter.toDTO(entity, ClassDTO.class);

    List<User> users = classUserRepository.findUserByClassId(entity.getId());
    List<UserDTO> userDTOS = new ArrayList<>();
    for (User user : users) {
      UserDTO newUserDTO = (UserDTO) genericConverter.toDTO(user, UserDTO.class);
      userDTOS.add(newUserDTO);
    }
    result.setUserDTOs(userDTOS);
    return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
  }

  private void loadClassUserFromListUserId(List<UserDTO> requestUserDTOs, Long classId) {
    if (requestUserDTOs != null && !requestUserDTOs.isEmpty()) {
      for (UserDTO userDTO : requestUserDTOs) {
        ClassUser clu = new ClassUser();
        clu.setFamsClass(classRepository.findById(classId));
        clu.setUser(userRepository.findById(userDTO.getId()));
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
    List<FamsClass> entities = classRepository.findAllByOrderByIdDesc(pageable);
    List<ClassDTO> result = new ArrayList<>();
    for (FamsClass entity : entities) {
      ClassDTO newDTO = (ClassDTO) genericConverter.toDTO(entity, ClassDTO.class);
      result.add(newDTO);
    }
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
    Long duration = classDTO.getDuration();
    Long startDate = classDTO.getStartDate();
    Long endDate = classDTO.getEndDate();
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<FamsClass> entities = classRepository.searchSortFilter(code, name, duration, startDate, endDate, pageable);
    List<ClassDTO> result = new ArrayList<>();
    Long count = classRepository.countSearchSortFilter(code, name, duration, startDate, endDate);
    for (FamsClass entity : entities){
      ClassDTO newDTO = (ClassDTO) genericConverter.toDTO(entity, ClassDTO.class);
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
  public ResponseEntity<?> searchSortFilterADMIN(ClassDTO classDTO, String sortById, int page, int limit) {
    String code = classDTO.getCode();
    String name = classDTO.getName();
    Long duration = classDTO.getDuration();
    Long startDate = classDTO.getStartDate();
    Long endDate = classDTO.getEndDate();
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<FamsClass> entities = classRepository.searchSortFilterADMIN(code, name, duration, startDate, endDate, sortById, pageable);
    List<ClassDTO> result = new ArrayList<>();
    Long count = classRepository.countSearchSortFilter(code, name, duration, startDate, endDate);
    for (FamsClass entity : entities){
      ClassDTO newDTO = (ClassDTO) genericConverter.toDTO(entity, ClassDTO.class);
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
