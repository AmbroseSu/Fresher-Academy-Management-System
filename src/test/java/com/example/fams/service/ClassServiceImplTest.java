//package com.example.fams.service;
//
//import com.example.fams.config.CustomValidationException;
//import com.example.fams.converter.GenericConverter;
//import com.example.fams.dto.ClassDTO;
//import com.example.fams.dto.ResponseDTO;
//import com.example.fams.entities.FamsClass;
//import com.example.fams.repository.*;
//import com.example.fams.services.impl.ClassServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//@ExtendWith(MockitoExtension.class)
//public class ClassServiceImplTest{
//
//  @Mock
//  private ClassRepository classRepository;
//  @Mock
//  private UserClassRepository userClassRepository;
//  @Mock
//  private ClassUserRepository classUserRepository;
//  @Mock
//  private UserRepository userRepository;
//  @Mock
//  private TrainingProgramRepository trainingProgramRepository;
//  @Mock
//  private GenericConverter genericConverter;
//
//  @InjectMocks
//  private ClassServiceImpl classService;
//
//  @Test
//  void findAllByStatusTrue_Success(){
//    int page = 1;
//    int limit = 10;
//    Pageable pageable = PageRequest.of(page - 1, limit);
//    List<FamsClass> entities = new ArrayList<>();
//    entities.add(new FamsClass());
//    entities.add(new FamsClass());
//
//    when(classRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
//    when(classRepository.countAllByStatusIsTrue()).thenReturn(2L);
//    when(genericConverter.toDTO(any(FamsClass.class), eq(ClassDTO.class)))
//        .thenReturn(new ClassDTO());
//    ResponseEntity<?> response = classService.findAllByStatusTrue(page, limit);
//    ResponseDTO responseDTO = (ResponseDTO) response.getBody();
//
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assert responseDTO != null;
//    assertEquals(2, ((List<?>) responseDTO.getContent()).size());
//    verify(classRepository, times(1)).findAllByStatusIsTrue(pageable);
//    verify(classRepository, times(1)).countAllByStatusIsTrue();
//    verify(genericConverter, times(entities.size())).toDTO(any(FamsClass.class),
//        eq(ClassDTO.class));
//
//
//  }
//
//  @Test
//  void findAllByStatusTrue_EmptyResult() {
//    // Arrange
//    int page = 1;
//    int limit = 10;
//    Pageable pageable = PageRequest.of(page - 1, limit);
//
//    // * Tạo mock data thay thế database
//    List<FamsClass> entities = new ArrayList<>();
//
//    // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
//    when(classRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
//    when(classRepository.countAllByStatusIsTrue()).thenReturn(0L);
//
//    // * Test hàm trong service
//    ResponseEntity<?> response = classService.findAllByStatusTrue(page, limit);
//    ResponseDTO responseDTO = (ResponseDTO) response.getBody();
//
//    // * Kiểm tra kết quả trả về
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assert responseDTO != null;
//    assertEquals(0, ((List<?>) responseDTO.getContent()).size());
//    verify(classRepository, times(1)).findAllByStatusIsTrue(pageable);
//    verify(classRepository, times(1)).countAllByStatusIsTrue();
//  }
//
//  @Test
//  void testFindById_ExistingClass() {
//    // * Tạo mock data thay thế database
//    Long id = 1L;
//    FamsClass famsClass = new FamsClass();
//    famsClass.setId(id);
//    famsClass.setStatus(true);
//
//    ClassDTO classDTO = new ClassDTO();
//    classDTO.setId(id);
//
//    // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
//    when(classRepository.findByStatusIsTrueAndId(id)).thenReturn(famsClass);
//    when(genericConverter.toDTO(famsClass, ClassDTO.class)).thenReturn(classDTO);
//
//    // * Test hàm trong service
//    ResponseEntity<?> response = classService.findById(id);
//    ResponseDTO responseDTO = (ResponseDTO) response.getBody();
//    ClassDTO result = (ClassDTO) responseDTO.getContent();
//
//    // * Kiểm tra kết quả trả về
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
//    assertEquals(id, result.getId());
//  }
//
//
//  @Test
//  void testFindById_NonExistingClass() {
//    // Arrange
//    Long id = 1L;
//    when(classRepository.findByStatusIsTrueAndId(id)).thenReturn(null);
//
//    // * Test hàm trong service
//    ResponseEntity<?> response = classService.findById(id);
//    ResponseDTO responseDTO = (ResponseDTO) response.getBody();
//
//    // * Kiểm tra kết quả trả về
//    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    assertEquals("Class not found", responseDTO.getDetails().get(0));
//  }
//
//  @Test
//  void testCreateNewClass_returnSuccess() {
//    ClassDTO classDTO = new ClassDTO();
//    //List<FamsClass> entities = new ArrayList();
//    //entities.add(new FamsClass());
//    //entities.add(new FamsClass());
//    // Arrange
//    classDTO.setName("Class");
//    classDTO.setCode("321");
//    classDTO.setFsu("231");
//    classDTO.setStatus(true);
//    classDTO.setStartDate(1733961600L);
//    classDTO.setEndDate(1736640000L);
//    classDTO.setStartTimeFrame(LocalTime.parse("20:00:00"));
//    classDTO.setEndTimeFrame(LocalTime.parse("21:00:00"));
//    classDTO.setUserIds(null);
//   // ClassDTO classDTO = new ClassDTO();
//    // Mock behavior
//    when(classRepository.save(any())).thenReturn(new FamsClass());
//    when(genericConverter.toDTO(any(FamsClass.class), eq(ClassDTO.class)))
//        .thenReturn(new ClassDTO());
//
//    // Act
//    ResponseEntity<?> responseEntity = classService.save(classDTO);
//    ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
//    // Assert
//    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    // Add more assertions as needed
//    assertEquals("Saved successfully", responseDTO.getDetails().get(0));
//  }
//
//  @Test
//  void testCreateNewClass_ReturnUserIdsNotExist() {
//    ClassDTO classDTO = new ClassDTO();
//    classDTO.setName("Class");
//    classDTO.setCode("321");
//    classDTO.setFsu("231");
//    classDTO.setStatus(true);
//    classDTO.setStartDate(1733961600L);
//    classDTO.setEndDate(1736640000L);
//    classDTO.setStartTimeFrame(LocalTime.parse("20:00:00"));
//    classDTO.setEndTimeFrame(LocalTime.parse("21:00:00"));
//    classDTO.setUserIds(Collections.singletonList(1L)); // Assuming syllabusId 1 doesn't exist
//
//    // Using Mockito.lenient() to avoid UnnecessaryStubbingException
//    Mockito.lenient().when(userRepository.existsById(1L)).thenReturn(false);
//
//    // Act & Assert
//    CustomValidationException exception = assertThrows(CustomValidationException.class,
//        () -> classService.save(classDTO));
//    assertEquals("User with id 1 does not exist", exception.getMessage());
//
//  }
//
//
//
//
//  // doi fix, chua hoan thien
//  @Test
//  void testUpdateClass_returnSuccess() {
//    // Prepare the test data and mock responses
//    FamsClass famsClass = new FamsClass();
//    famsClass.setId(1L); // Assuming an existing class with ID 1
//    ClassDTO classDTO = new ClassDTO();
//    classDTO.setStartDate(1733961600L); // Example start date
//    classDTO.setEndDate(1736640000L); // Example end date
//
//    // Mock the necessary methods
//    Mockito.lenient().when(classRepository.findById(anyLong())).thenReturn(famsClass); // Corrected to return an Optional
//    when(classRepository.save(any(FamsClass.class))).thenReturn(famsClass); // Ensure the save method returns a FamsClass object, not TrainingProgram
//    when(genericConverter.toDTO(any(FamsClass.class), eq(ClassDTO.class))).thenReturn(classDTO); // Corrected to convert FamsClass to ClassDTO
//
//    // Execute the method under test
//    ResponseEntity<?> responseEntity = classService.save(classDTO);
//    ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
//
//    // Verify the outcomes
//    assertEquals(HttpStatus.OK, responseEntity.getStatusCode()); // Check if the status code is OK
//    assertEquals("Saved successfully", responseDTO.getDetails().get(0)); // Verify the success message
//  }
//
//  @Test
//  void testChangeStatus_ClassNotFound() {
//    // Given
//    Long id = 1L;
//    when(classRepository.findById(id)).thenReturn(null);
//
//    // When
//    ResponseEntity<?> responseEntity = classService.changeStatus(id);
//    ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
//    // Then
//    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
//    assertEquals("Class not found", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
//    assertEquals("Cannot change status of non-existing Class", (responseDTO.getMessage()));
//    verify(classRepository, never()).save(any()); // Ensure save is not called
//  }
//
//  @Test
//  public void testChangeStatus_StatusChangedSuccessfully() {
//    // Given
//    Long id = 1L;
//    FamsClass famsClass = new FamsClass();
//    famsClass.setId(id);
//    famsClass.setStatus(true); // Initial status is true
//    when(classRepository.findById(id)).thenReturn(famsClass);
//
//    // When
//    ResponseEntity<?> responseEntity = classService.changeStatus(id);
//
//    // Then
//    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    assertEquals("Status changed successfully", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
//    assertEquals(false, famsClass.getStatus()); // Ensure status is toggled
//    verify(classRepository, times(1)).save(famsClass); // Verify that save is called once
//  }
//
//
//
//}
