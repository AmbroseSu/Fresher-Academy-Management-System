package com.example.fams.service;

import com.example.fams.config.CustomValidationException;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ClassCalendarDTO;
import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.entities.CalendarClass;
import com.example.fams.entities.FamsClass;
import com.example.fams.entities.TrainingProgram;
import com.example.fams.entities.User;
import com.example.fams.entities.enums.WeekDay;
import com.example.fams.repository.*;
import com.example.fams.services.impl.ClassServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ClassServiceImplTest{

  @Mock
  private CalendarRepository calendarRepository;
  @Mock
  private ClassRepository classRepository;
  @Mock
  private UserClassRepository userClassRepository;
  @Mock
  private ClassUserRepository classUserRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private TrainingProgramRepository trainingProgramRepository;
  @Mock
  private GenericConverter genericConverter;

  @InjectMocks
  private ClassServiceImpl classService;

  @Test
  void findAllByStatusTrue_Success(){
    int page = 1;
    int limit = 10;
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<FamsClass> entities = new ArrayList<>();
    entities.add(new FamsClass());
    entities.add(new FamsClass());

    when(classRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
    when(classRepository.countAllByStatusIsTrue()).thenReturn(2L);
    when(genericConverter.toDTO(any(FamsClass.class), eq(ClassDTO.class)))
        .thenReturn(new ClassDTO());
    ResponseEntity<?> response = classService.findAllByStatusTrue(page, limit);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assert responseDTO != null;
    assertEquals(2, ((List<?>) responseDTO.getContent()).size());
  }

  @Test
  void findAllByStatusTrue_EmptyResult() {
    // Arrange
    int page = 1;
    int limit = 10;
    Pageable pageable = PageRequest.of(page - 1, limit);

    // * Tạo mock data thay thế database
    List<FamsClass> entities = new ArrayList<>();

    // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
    when(classRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
    when(classRepository.countAllByStatusIsTrue()).thenReturn(0L);

    // * Test hàm trong service
    ResponseEntity<?> response = classService.findAllByStatusTrue(page, limit);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();

    // * Kiểm tra kết quả trả về
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assert responseDTO != null;
    assertEquals(0, ((List<?>) responseDTO.getContent()).size());
    verify(classRepository, times(1)).findAllByStatusIsTrue(pageable);
    verify(classRepository, times(1)).countAllByStatusIsTrue();
  }

  @Test
  void testFindById_ExistingClass() {
    // * Tạo mock data thay thế database
    Long id = 1L;
    FamsClass famsClass = new FamsClass();
    famsClass.setId(id);
    famsClass.setStatus(true);

    ClassDTO classDTO = new ClassDTO();
    classDTO.setId(id);

    // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
    when(classRepository.findByStatusIsTrueAndId(id)).thenReturn(famsClass);
    when(genericConverter.toDTO(famsClass, ClassDTO.class)).thenReturn(classDTO);

    // * Test hàm trong service
    ResponseEntity<?> response = classService.findById(id);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();
    ClassDTO result = (ClassDTO) responseDTO.getContent();

    // * Kiểm tra kết quả trả về
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
    assertEquals(id, result.getId());
  }


  @Test
  void testFindById_NonExistingClass() {
    // Arrange
    Long id = 1L;
    when(classRepository.findByStatusIsTrueAndId(id)).thenReturn(null);

    // * Test hàm trong service
    ResponseEntity<?> response = classService.findById(id);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();

    // * Kiểm tra kết quả trả về
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Class not found", responseDTO.getDetails().get(0));
  }

  @Test
  void testCreateNewClass_returnSuccess() {
    // Arrange
    ClassDTO classDTO = new ClassDTO();
    classDTO.setName("Class");
    classDTO.setCode("321");
    classDTO.setFsu("231");
    classDTO.setStatus(true);
    classDTO.setStartDate(1733961600L);
    classDTO.setEndDate(1736640000L);
    classDTO.setAdminIds(Collections.emptyList());
    classDTO.setTrainerIds(Collections.emptyList());
    classDTO.setTrainingProgramId(null);

    FamsClass savedFamsClass = new FamsClass();
    savedFamsClass.setId(1L);

    ClassDTO expectedClassDTO = new ClassDTO();
    expectedClassDTO.setId(1L);
    expectedClassDTO.setName("Class");
    expectedClassDTO.setCode("321");
    expectedClassDTO.setFsu("231");
    expectedClassDTO.setStatus(true);
    expectedClassDTO.setStartDate(1733961600L);
    expectedClassDTO.setEndDate(1736640000L);
    expectedClassDTO.setAdminIds(Collections.emptyList());
    expectedClassDTO.setTrainerIds(Collections.emptyList());
    expectedClassDTO.setTrainingProgramId(null);

    // Mock behavior
    when(classRepository.save(any(FamsClass.class))).thenReturn(savedFamsClass);
    when(genericConverter.toDTO(any(FamsClass.class), eq(ClassDTO.class))).thenReturn(expectedClassDTO);

    // Act
    ResponseEntity<?> responseEntity = classService.save(classDTO);
    ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

    // Assert
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Saved successfully", responseDTO.getDetails().get(0));
    ClassDTO actualClassDTO = (ClassDTO) responseDTO.getContent();
    assertEquals(expectedClassDTO.getId(), actualClassDTO.getId());
    assertEquals(expectedClassDTO.getName(), actualClassDTO.getName());
    assertEquals(expectedClassDTO.getCode(), actualClassDTO.getCode());
    assertEquals(expectedClassDTO.getFsu(), actualClassDTO.getFsu());
    assertEquals(expectedClassDTO.getStatus(), actualClassDTO.getStatus());
    assertEquals(expectedClassDTO.getStartDate(), actualClassDTO.getStartDate());
    assertEquals(expectedClassDTO.getEndDate(), actualClassDTO.getEndDate());
    assertEquals(expectedClassDTO.getAdminIds(), actualClassDTO.getAdminIds());
    assertEquals(expectedClassDTO.getTrainerIds(), actualClassDTO.getTrainerIds());
    assertEquals(expectedClassDTO.getTrainingProgramId(), actualClassDTO.getTrainingProgramId());
  }


  @Test
  void testCreateNewClass_ReturnUserIdsNotExist() {
    ClassDTO classDTO = new ClassDTO();
    classDTO.setName("Class");
    classDTO.setCode("321");
    classDTO.setFsu("231");
    classDTO.setStatus(true);
    classDTO.setStartDate(1733961600L);
    classDTO.setEndDate(1736640000L);
    classDTO.setStartTime(LocalTime.parse("20:00:00"));
    classDTO.setEndTime(LocalTime.parse("21:00:00"));
    classDTO.setAdminIds(Collections.singletonList(1L));
    classDTO.setTrainerIds(Collections.singletonList(1L));// Assuming syllabusId 1 doesn't exist

    // Using Mockito.lenient() to avoid UnnecessaryStubbingException
    Mockito.lenient().when(userRepository.existsById(1L)).thenReturn(false);

    // Act & Assert
    CustomValidationException exception = assertThrows(CustomValidationException.class,
        () -> classService.save(classDTO));
    assertEquals("User with id 1 does not exist, User with id 1 does not exist", exception.getMessage());

  }


  @Test
  void testSaveClass_returnSuccess() {
    // Prepare the test data
    ClassDTO classDTO = new ClassDTO();
    classDTO.setId(1L); // Assuming an existing class with ID 1
    classDTO.setName("Updated Class");
    classDTO.setCode("UC001");
    classDTO.setFsu("FSU001");
    classDTO.setStatus(true);
    classDTO.setStartDate(1733961600L);
    classDTO.setEndDate(1736640000L);
    classDTO.setAdminIds(Arrays.asList(1L, 2L));
    classDTO.setTrainerIds(Arrays.asList(3L, 4L));
    classDTO.setTrainingProgramId(1L);

    FamsClass famsClass = new FamsClass();
    famsClass.setId(1L);
    famsClass.setName("Updated Class");
    famsClass.setCode("UC001");
    famsClass.setFsu("FSU001");
    famsClass.setStatus(true);
    famsClass.setStartDate(1L);
    famsClass.setEndDate(2L);
    // Mock the Authentication object
    Authentication authentication = Mockito.mock(Authentication.class);
    // Mock the getName() method to return a dummy username
    Mockito.when(authentication.getName()).thenReturn("testUser");
    // Set the mock Authentication object to the SecurityContext
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    // Mock the necessary methods
    when(classRepository.findById(1L)).thenReturn(famsClass);
    when(classRepository.save(any(FamsClass.class))).thenReturn(famsClass);
    when(genericConverter.toDTO(any(FamsClass.class), eq(ClassDTO.class))).thenReturn(classDTO);

    // Mock the userRepository.findById() method to return non-null values for the specified user IDs
    when(userRepository.existsById(1L)).thenReturn(true);
    when(userRepository.existsById(2L)).thenReturn(true);
    when(userRepository.existsById(3L)).thenReturn(true);
    when(userRepository.existsById(4L)).thenReturn(true);
    when(trainingProgramRepository.existsById(1L)).thenReturn(true);

    // Execute the method under test
    ResponseEntity<?> responseEntity = classService.save(classDTO);
    ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

    // Verify the outcomes
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Saved successfully", responseDTO.getDetails().get(0));

    ClassDTO savedClassDTO = (ClassDTO) responseDTO.getContent();
    assertEquals(classDTO.getId(), savedClassDTO.getId());
    assertEquals(classDTO.getName(), savedClassDTO.getName());
    assertEquals(classDTO.getCode(), savedClassDTO.getCode());
    assertEquals(classDTO.getFsu(), savedClassDTO.getFsu());
    assertEquals(classDTO.getStatus(), savedClassDTO.getStatus());
    assertEquals(classDTO.getStartDate(), savedClassDTO.getStartDate());
    assertEquals(classDTO.getEndDate(), savedClassDTO.getEndDate());
    assertEquals(classDTO.getAdminIds(), savedClassDTO.getAdminIds());
    assertEquals(classDTO.getTrainerIds(), savedClassDTO.getTrainerIds());
    assertEquals(classDTO.getTrainingProgramId(), savedClassDTO.getTrainingProgramId());
  }


  @Test
  void testChangeStatus_ClassNotFound() {
    // Given
    Long id = 1L;
    when(classRepository.findById(id)).thenReturn(null);

    // When
    ResponseEntity<?> responseEntity = classService.changeStatus(id);
    ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
    // Then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertEquals("Class not found", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
    assertEquals("Cannot change status of non-existing Class", (responseDTO.getMessage()));
    verify(classRepository, never()).save(any()); // Ensure save is not called
  }

  @Test
  public void testChangeStatus_StatusChangedSuccessfully() {
    // Given
    Long id = 1L;
    FamsClass famsClass = new FamsClass();
    famsClass.setId(id);
    famsClass.setStatus(true); // Initial status is true
    when(classRepository.findById(id)).thenReturn(famsClass);

    // When
    ResponseEntity<?> responseEntity = classService.changeStatus(id);

    // Then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Status changed successfully", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
    assertEquals(false, famsClass.getStatus()); // Ensure status is toggled
    verify(classRepository, times(1)).save(famsClass); // Verify that save is called once
  }

  @Test
  void testSearchBetweenStartDateAndEndDate_Success() {
    // Arrange
    Long dayStartWeek = 1733961600L;
    Long dayEndWeek = 1736640000L;
    int page = 1;
    int limit = 10;
    Pageable pageable = PageRequest.of(page - 1, limit);

    List<FamsClass> entities = new ArrayList<>();
    FamsClass famsClass1 = new FamsClass();
    famsClass1.setId(1L);
    entities.add(famsClass1);
    FamsClass famsClass2 = new FamsClass();
    famsClass2.setId(2L);
    entities.add(famsClass2);

    List<CalendarClass> calendarClasses1 = new ArrayList<>();
    CalendarClass calendarClass1 = new CalendarClass();
    calendarClass1.setWeekDays(WeekDay.Monday);
    calendarClasses1.add(calendarClass1);

    List<CalendarClass> calendarClasses2 = new ArrayList<>();
    CalendarClass calendarClass2 = new CalendarClass();
    calendarClass2.setWeekDays(WeekDay.Tuesday);
    calendarClasses2.add(calendarClass2);

    when(classRepository.searchBetweenStartDateAndEndDate(dayStartWeek, dayEndWeek, pageable))
            .thenReturn(entities);
    when(classRepository.countSearchBetweenStartDateAndEndDate(dayStartWeek, dayEndWeek))
            .thenReturn(2L);
    when(genericConverter.toDTO(famsClass1, ClassDTO.class)).thenReturn(new ClassDTO());
    when(genericConverter.toDTO(famsClass2, ClassDTO.class)).thenReturn(new ClassDTO());
    when(calendarRepository.findByFamsClassId(1L)).thenReturn(calendarClasses1);
    when(calendarRepository.findByFamsClassId(2L)).thenReturn(calendarClasses2);

    // Act
    ResponseEntity<?> response = classService.searchBetweenStartDateAndEndDate(dayStartWeek, dayEndWeek, page, limit);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
    List<ClassCalendarDTO> classCalendarDTOS = (List<ClassCalendarDTO>) responseDTO.getContent();
    assertEquals(2, classCalendarDTOS.size());
  }

  @Test
  void testSearchBetweenStartDateAndEndDate_Exception() {
    // Arrange
    Long dayStartWeek = 1733961600L;
    Long dayEndWeek = 1736640000L;
    int page = 1;
    int limit = 10;
    Pageable pageable = PageRequest.of(page - 1, limit);

    // Mock the classRepository to throw an exception
    when(classRepository.searchBetweenStartDateAndEndDate(dayStartWeek, dayEndWeek, pageable))
            .thenThrow(new RuntimeException("Database error"));

    // Act
    ResponseEntity<?> response = classService.searchBetweenStartDateAndEndDate(dayStartWeek, dayEndWeek, page, limit);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testSaveWithCalendar_Success() {
    // Arrange
    ClassDTO classDTO = new ClassDTO();
    classDTO.setName("Test Class");
    classDTO.setStartDate(1711904400L);
    classDTO.setEndDate(1714237200L);
    classDTO.setStartTime(LocalTime.of(12, 0));
    classDTO.setEndTime(LocalTime.of(16, 0));
    classDTO.setDuration(32D);
    classDTO.setAdminIds(List.of(1L, 2L));
    classDTO.setTrainerIds(List.of(3L, 4L));
    classDTO.setTrainingProgramId(1L);

    List<WeekDay> weekDays = List.of(WeekDay.Monday, WeekDay.Wednesday);
    ClassDTO expectedClassDTO = new ClassDTO();
    FamsClass famsClass = new FamsClass();
    famsClass.setId(1L);

    when(userRepository.existsById(anyLong())).thenReturn(true);
    when(trainingProgramRepository.existsById(anyLong())).thenReturn(true);
    when(classRepository.findAll()).thenReturn(Collections.emptyList());
    when(classRepository.save(any(FamsClass.class))).thenReturn(famsClass);
    when(genericConverter.toDTO(any(FamsClass.class), eq(ClassDTO.class))).thenReturn(expectedClassDTO);

    // Act
    ResponseEntity<?> response = classService.save_withCalendar(classDTO, weekDays);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Saved successfully", responseDTO.getDetails().get(0));
  }

  @Test
  void testSaveWithCalendar_InvalidSlotCount() {
    // Arrange
    ClassDTO classDTO = new ClassDTO();
    classDTO.setName("Test Class");
    classDTO.setStartDate(1711904400L);
    classDTO.setEndDate(1714237200L);
    classDTO.setStartTime(LocalTime.of(12, 0));
    classDTO.setEndTime(LocalTime.of(16, 0));
    classDTO.setDuration(32D);
    classDTO.setAdminIds(List.of(1L, 2L));
    classDTO.setTrainerIds(List.of(3L, 4L));
    classDTO.setTrainingProgramId(1L);

    List<WeekDay> weekDays = List.of(WeekDay.Monday, WeekDay.Wednesday, WeekDay.Saturday);
    ClassDTO expectedClassDTO = new ClassDTO();
    FamsClass famsClass = new FamsClass();
    famsClass.setId(1L);

    when(userRepository.existsById(anyLong())).thenReturn(true);
    when(trainingProgramRepository.existsById(anyLong())).thenReturn(true);
    when(classRepository.findAll()).thenReturn(Collections.emptyList());
    when(classRepository.save(any(FamsClass.class))).thenReturn(famsClass);
    when(genericConverter.toDTO(any(FamsClass.class), eq(ClassDTO.class))).thenReturn(expectedClassDTO);

    // Act
    ResponseEntity<?> response = classService.save_withCalendar(classDTO, weekDays);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(responseDTO.getDetails().get(0).startsWith("Amount of slot per week required:"));
    assertEquals("Amount of slot per week required: 2", responseDTO.getDetails().get(0));
  }

  @Test
  void testSaveWithCalendar_Exception() {
    // Arrange
    ClassDTO classDTO = new ClassDTO();
    classDTO.setName("Test Class");
    classDTO.setStartDate(1711904400L);
    classDTO.setEndDate(1714237200L);
    classDTO.setStartTime(LocalTime.of(12, 0));
    classDTO.setEndTime(LocalTime.of(16, 0));
    classDTO.setDuration(32D);
    classDTO.setAdminIds(List.of(1L, 2L));
    classDTO.setTrainerIds(List.of(3L, 4L));
    classDTO.setTrainingProgramId(1L);

    List<WeekDay> weekDays = List.of(WeekDay.Monday, WeekDay.Wednesday, WeekDay.Saturday);
    ClassDTO expectedClassDTO = new ClassDTO();
    FamsClass famsClass = new FamsClass();
    famsClass.setId(1L);

    when(userRepository.existsById(anyLong())).thenReturn(true);
    when(trainingProgramRepository.existsById(anyLong())).thenReturn(true);
    when(classRepository.findAll()).thenReturn(Collections.emptyList());
    when(classRepository.save(any(FamsClass.class))).thenReturn(famsClass);
    when(genericConverter.toDTO(any(FamsClass.class), eq(ClassDTO.class))).thenReturn(null);

    // Act
    ResponseEntity<?> response = classService.save_withCalendar(classDTO, weekDays);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }


}
