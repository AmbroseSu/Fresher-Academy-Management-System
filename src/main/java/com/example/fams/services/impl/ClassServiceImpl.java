package com.example.fams.services.impl;

import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.CalendarDTO;
import com.example.fams.dto.ClassCalendarDTO;
import com.example.fams.dto.ClassDTO;
import com.example.fams.entities.CalendarClass;
import com.example.fams.entities.ClassUser;
import com.example.fams.entities.FamsClass;
import com.example.fams.entities.TrainingProgram;
import com.example.fams.entities.User;
import com.example.fams.entities.enums.WeekDay;
import com.example.fams.entities.enums.Role;
import com.example.fams.repository.*;
import com.example.fams.services.IClassService;
import com.example.fams.services.ServiceUtils;
import java.time.Duration;
import java.util.Calendar;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("ClassService")
public class ClassServiceImpl implements IClassService {

  private final ClassRepository classRepository;
  private final CalendarRepository calendarRepository;
  private final UserClassRepository userClassRepository;
  private final ClassUserRepository classUserRepository;
  private final UserRepository userRepository;
  private final TrainingProgramRepository trainingProgramRepository;
  private final GenericConverter genericConverter;

  public ClassServiceImpl(ClassRepository classRepository,
      CalendarRepository calendarRepository, UserClassRepository userClassRepository,
                          ClassUserRepository classUserRepository, UserRepository userRepository, TrainingProgramRepository trainingProgramRepository, GenericConverter genericConverter) {
    this.classRepository = classRepository;
    this.calendarRepository = calendarRepository;
    this.userClassRepository = userClassRepository;
    this.classUserRepository = classUserRepository;
    this.userRepository = userRepository;
    this.trainingProgramRepository = trainingProgramRepository;
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
    List<Long> requestUserIds = Stream.concat(
            classDTO.getAdminIds().stream(),
            classDTO.getTrainerIds().stream())
            .toList();
    Long requestTrainingProgramId = classDTO.getTrainingProgramId();
    FamsClass entity;

    // * Validate requestDTO ( if left null, then can be updated later )
    if (requestUserIds != null && !requestUserIds.isEmpty()){
      ServiceUtils.validateUserIds(requestUserIds, userRepository);
    }
    if (requestTrainingProgramId != null){
      ServiceUtils.validateTrainingProgramIds(List.of(requestTrainingProgramId), trainingProgramRepository);
    }
    if (classDTO.getId() == null){
      ServiceUtils.validateStartDateBeforeEndDate(classDTO);
      //ServiceUtils.validateStartDateWhenSameTimeFrame(classDTO, classRepository);
    }
    if (!ServiceUtils.errors.isEmpty()) {
      throw new CustomValidationException(ServiceUtils.errors);
    }


    // * For update request
    if (classDTO.getId() != null){
      FamsClass oldEntity = classRepository.findById(classDTO.getId());
      FamsClass tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
      entity = convertDtoToEntity(classDTO, trainingProgramRepository);
      ServiceUtils.fillMissingAttribute(entity, tempOldEntity);
      classUserRepository.deleteAllByFamsClassId(classDTO.getId());
      loadClassUserFromListUserId(requestUserIds, entity.getId());
      entity.markModified();
      classRepository.save(entity);
    }

    // * For create request
    else {
      classDTO.setStatus(true);
      entity = convertDtoToEntity(classDTO, trainingProgramRepository);
      classRepository.save(entity);
      loadClassUserFromListUserId(requestUserIds, entity.getId());
    }


    ClassDTO result = convertClassToClassDTO(entity);
    if (classDTO.getId() == null){
      result.setAdminIds(classDTO.getAdminIds());
      result.setTrainerIds(classDTO.getTrainerIds());
    }
    result.setTrainingProgramId(requestTrainingProgramId);
    return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
  }

  private void loadClassUserFromListUserId(List<Long> requestUserIds, Long classId) {
    if (requestUserIds != null && !requestUserIds.isEmpty()) {
      for (Long userId : requestUserIds) {
        User user = userRepository.findById(userId);
        FamsClass famsClass = classRepository.findById(classId);
        if (user != null && famsClass != null) {
          ClassUser clu = new ClassUser();
          clu.setUser(user);
          clu.setFamsClass(famsClass);
          classUserRepository.save(clu);
        }
      }
    }
  }

  @Override
  public ResponseEntity<?> findById(Long id) {
    FamsClass entity = classRepository.findByStatusIsTrueAndId(id);
    if (entity != null) {
      ClassDTO result = convertClassToClassDTO(entity);
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
  public ResponseEntity<?> changeStatusClassCalendar(Long id) {
    FamsClass famsClass = classRepository.findById(id);
    if (famsClass != null) {
      if (famsClass.getStatus()) {
        famsClass.setStatus(false);
      } else {
        famsClass.setStatus(true);
      }
      List<CalendarClass> calendarsClasses = calendarRepository.findByFamsClassId(id);
      if (!calendarsClasses.isEmpty()){
        for (CalendarClass calendarClass : calendarsClasses){
          if(calendarClass.getStatus()){
            calendarClass.setStatus(false);
          }else{
            calendarClass.setStatus(true);
          }
          calendarRepository.save(calendarClass);
        }
      }else{
        return ResponseUtil.error("Calendar not found", "Cannot change status of non-existing Calendar", HttpStatus.NOT_FOUND);
      }

      classRepository.save(famsClass);
      return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
    } else {
      return ResponseUtil.error("Class not found", "Cannot change status of non-existing Class", HttpStatus.NOT_FOUND);
    }
  }


  @Override
  public Boolean checkExist(Long id) {
    FamsClass famsClass = classRepository.findById(id);
    return famsClass != null;

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




  @Override
  public ResponseEntity<?> searchBetweenStartDateAndEndDate(Long dayStartWeek, Long dayEndWeek, int page, int limit) {
    try {
      Pageable pageable = PageRequest.of(page - 1, limit);
      List<FamsClass> entities = classRepository.searchBetweenStartDateAndEndDate(dayStartWeek,
          dayEndWeek, pageable);
      List<ClassDTO> result = new ArrayList<>();
      Long count = classRepository.countSearchBetweenStartDateAndEndDate(dayStartWeek, dayEndWeek);
      convertListClassToListClassDTO(entities, result);
      List<ClassCalendarDTO> classCalendarDTOS = new ArrayList<>();
      ClassCalendarDTO classCalendarDTO = new ClassCalendarDTO();
      for (ClassDTO classDTO : result) {
        List<CalendarClass> calendarClasses = calendarRepository.findByFamsClassId(
            classDTO.getId());
        List<WeekDay> weekDays = new ArrayList<>();
        //convertListCalendarClasstoDto(calendarClasses, calendarDTOS);
        for(CalendarClass calendarClass : calendarClasses){
          weekDays.add(calendarClass.getWeekDays());
        }
        classCalendarDTO.setClassDTO(classDTO);
        classCalendarDTO.setWeekDays(weekDays);
        classCalendarDTOS.add(classCalendarDTO);

      }
      return ResponseUtil.getCollection(classCalendarDTOS,
          HttpStatus.OK,
          "Fetched successfully",
          page,
          limit,
          count);

    } catch (Exception e){
      return ResponseUtil.error(e.getMessage(),e.toString(),HttpStatus.BAD_REQUEST);
    }
  }

//  private void convertListCalendarClasstoDto(List<CalendarClass> calendarClasses, List<CalendarDTO> calendarDTOS){
//    for(CalendarClass calendarClass : calendarClasses){
//      CalendarDTO calendarDTO = convertCalendarToCalendarDto(calendarClass);
//      calendarDTOS.add(calendarDTO);
//    }
//  }
//
//  private CalendarDTO convertCalendarToCalendarDto(CalendarClass calendarClass){
//    CalendarDTO calendarDTO = (CalendarDTO) genericConverter.toDTO(calendarClass,CalendarDTO.class);
//    return calendarDTO;
//  }


  private void convertListClassToListClassDTO(List<FamsClass> entities, List<ClassDTO> result) {
    for (FamsClass famsClass : entities){
      ClassDTO newClassDTO = convertClassToClassDTO(famsClass);
      result.add(newClassDTO);
    }
  }

  private ClassDTO convertClassToClassDTO(FamsClass entity) {
    ClassDTO newClassDTO = (ClassDTO) genericConverter.toDTO(entity, ClassDTO.class);
    List<User> users = classUserRepository.findUserByClassId(entity.getId());
    if (entity.getClassUsers() == null){
      newClassDTO.setAdminIds(null);
      newClassDTO.setTrainerIds(null);
    }
    else {
      // ! Set list learningObjectiveIds và unitId sau khi convert ở trên vào contentDTO
      List<Long> adminIds = users.stream()
              .filter(user -> user.getUserRole().getRole().equals(Role.CLASSADMIN))
              .map(User::getId)
              .collect(Collectors.toList());
      newClassDTO.setAdminIds(adminIds);
      List<Long> trainerIds = users.stream()
              .filter(user -> user.getUserRole().getRole().equals(Role.TRAINER))
              .map(User::getId)
              .collect(Collectors.toList());
      newClassDTO.setTrainerIds(trainerIds);
    }
    return newClassDTO;
  }

  public FamsClass convertDtoToEntity(ClassDTO classDTO, TrainingProgramRepository trainingProgramRepository) {
    FamsClass famsClass = new FamsClass();
    famsClass.setId(classDTO.getId());
    famsClass.setName(classDTO.getName());
    famsClass.setCode(classDTO.getCode());
    famsClass.setStatus(classDTO.getStatus());
    famsClass.setDuration(classDTO.getDuration());
    famsClass.setFsu(classDTO.getFsu());
    famsClass.setClassStatus(classDTO.getClassStatus());
    famsClass.setLocation(classDTO.getLocation());
    famsClass.setStartDate(classDTO.getStartDate());
    famsClass.setEndDate(classDTO.getEndDate());
    famsClass.setStartTime(classDTO.getStartTime());
    famsClass.setEndTime(classDTO.getEndTime());
    famsClass.setAttendee(classDTO.getAttendee());
    famsClass.setAttendeeAccepted(classDTO.getAttendeeAccepted());
    famsClass.setAttendeeActual(classDTO.getAttendeeActual());
    famsClass.setAttendeePlanned(classDTO.getAttendeePlanned());
    TrainingProgram trainingProgram = trainingProgramRepository.findOneById(classDTO.getTrainingProgramId());
    famsClass.setTrainingProgram(trainingProgram);

    return famsClass;
  }
  public CalendarClass convertCalendarDtoToEntity(CalendarDTO calendarDTO, ClassRepository classRepository) {
    CalendarClass calendarClass = new CalendarClass();
    calendarClass.setId(calendarDTO.getId());
    calendarClass.setStatus(calendarDTO.getStatus());
    calendarClass.setWeekDays(calendarDTO.getWeekDay());
    calendarClass.setFamsClass(classRepository.findById(calendarDTO.getFamsClassIds()));

    return calendarClass;
  }


  public ResponseEntity<?> save_withCalendar(ClassDTO classDTO, List<WeekDay> weekDays){
    Double week = (double) calculateWeeks(classDTO.getStartDate(),classDTO.getEndDate());
    Duration duration = Duration.between(classDTO.getStartTime(), classDTO.getEndTime());
    // Lấy số giờ, phút và giây từ duration
    double hours = duration.toHours();
    double minutes = duration.toMinutesPart();
    double hoursOfSlot = hours + minutes/60;
    double numberOfSlotOneWeek;
    try{
      numberOfSlotOneWeek = (classDTO.getDuration()/hoursOfSlot)/week;
      if(numberOfSlotOneWeek % 1 != 0){
        return ResponseUtil.error("he number of slots in a week must be an a whole number","False",HttpStatus.BAD_REQUEST);
      }
      ServiceUtils.errors.clear();

      List<Long> requestUserIds = Stream.concat(
                      classDTO.getAdminIds().stream(),
                      classDTO.getTrainerIds().stream())
              .toList();
      Long requestTrainingProgramId = classDTO.getTrainingProgramId();
      FamsClass entity;
      CalendarClass calendar;
      CalendarDTO calendarDTO = new CalendarDTO();

      // * Validate requestDTO ( if left null, then can be updated later )
      if (requestUserIds != null){
        ServiceUtils.validateUserIds(requestUserIds, userRepository);
      }
      if (requestTrainingProgramId != null){
        ServiceUtils.validateTrainingProgramIds(List.of(requestTrainingProgramId), trainingProgramRepository);
      }
      if (classDTO.getId() == null){
        ServiceUtils.validateStartDateBeforeEndDate(classDTO);
        //ServiceUtils.validateStartDateWhenSameTimeFrame(classDTO, classRepository);
      }
      if (!ServiceUtils.errors.isEmpty()) {
        throw new CustomValidationException(ServiceUtils.errors);
      }

      List<FamsClass> allClass = classRepository.findAll();
        if (classDTO.getId() != null){
          String oldClassName = classDTO.getName();
          for(FamsClass famsClass : allClass) {
            if (!(oldClassName.equals(famsClass.getName()) && famsClass.getId().equals(classDTO.getId()))) {
              if (famsClass.getName().equals(classDTO.getName())) {
                return ResponseUtil.error("Class name has exists", "Error", HttpStatus.BAD_REQUEST);
              }
            }
          }
          FamsClass oldEntity = classRepository.findById(classDTO.getId());
          FamsClass tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
          entity = convertDtoToEntity(classDTO, trainingProgramRepository);
          ServiceUtils.fillMissingAttribute(entity, tempOldEntity);
          classUserRepository.deleteAllByFamsClassId(classDTO.getId());
          loadClassUserFromListUserId(requestUserIds, entity.getId());
          entity.markModified();
          classRepository.save(entity);
          return ResponseUtil.getObject(null, HttpStatus.OK, "Saved successfully");
        }

        // * For create request
        else {
          for(FamsClass famsClass : allClass){
            if(famsClass.getName().equals(classDTO.getName())){
              return ResponseUtil.error("Class name has exists","Error",HttpStatus.BAD_REQUEST);
            }
            if(famsClass.getLocation().equals(classDTO.getLocation())){

              if((famsClass.getStartTime().isBefore(classDTO.getStartTime()) && famsClass.getEndTime().isAfter(classDTO.getEndTime()))){
                return ResponseUtil.error("This Class has existed location and time frame","Create failed",HttpStatus.BAD_REQUEST);
              }
              if((famsClass.getStartTime().isBefore(classDTO.getEndTime()) && famsClass.getStartTime().isAfter(classDTO.getStartTime()))){
                return ResponseUtil.error("This Class has existed location and time frame","Create failed",HttpStatus.BAD_REQUEST);
              }
              if((famsClass.getStartTime().equals(classDTO.getStartTime()) && famsClass.getEndTime().equals(classDTO.getEndTime()))){
                return ResponseUtil.error("This Class has existed location and time frame","Create failed",HttpStatus.BAD_REQUEST);
              }
              if((famsClass.getEndTime().isBefore(classDTO.getEndTime()) && famsClass.getEndTime().isAfter(classDTO.getStartTime()))){
                return ResponseUtil.error("This Class has existed location and time frame","Create failed",HttpStatus.BAD_REQUEST);
              }
            }
          }
            if(numberOfSlotOneWeek == weekDays.size()) {
              classDTO.setStatus(true);
              entity = convertDtoToEntity(classDTO, trainingProgramRepository);
              classRepository.save(entity);
              for(WeekDay weekDay : weekDays) {
                calendarDTO.setWeekDay(weekDay);
                calendarDTO.setStatus(true);
                calendarDTO.setFamsClassIds(entity.getId());
                calendar = convertCalendarDtoToEntity(calendarDTO, classRepository);
                calendarRepository.save(calendar);
              }
              loadClassUserFromListUserId(requestUserIds, entity.getId());
              ClassDTO result = convertClassToClassDTO(entity);
              if (classDTO.getId() == null) {
                result.setAdminIds(classDTO.getAdminIds());
                result.setTrainerIds(classDTO.getTrainerIds());
              }
              result.setTrainingProgramId(requestTrainingProgramId);

              return ResponseUtil.getObject(null, HttpStatus.OK, "Saved successfully");
            }
          return ResponseUtil.error("Amount of slot per week required: " + (int) Math.floor(numberOfSlotOneWeek),"Wrong amount of slot per week",HttpStatus.BAD_REQUEST);

        }

    }catch (Exception e){
      return ResponseUtil.error("Error",e.getMessage(),HttpStatus.BAD_REQUEST);
    }
  }




  public static int calculateWeeks(Long startDay, Long endDay) {
    Calendar startCalendar = Calendar.getInstance();
    startCalendar.setTimeInMillis(startDay * 1000); // Chia cho 1000 để chuyển về đơn vị giây
    Calendar endCalendar = Calendar.getInstance();
    endCalendar.setTimeInMillis(endDay * 1000); // Chia cho 1000 để chuyển về đơn vị giây

    if (endCalendar.before(startCalendar)) {
      return -1; // Trả về giá trị âm để biểu thị lỗi
    }

    int weeks = 0;
    while (startCalendar.before(endCalendar)) {
      startCalendar.add(Calendar.WEEK_OF_YEAR, 1);
      weeks++;
    }
    return weeks;
  }


}
