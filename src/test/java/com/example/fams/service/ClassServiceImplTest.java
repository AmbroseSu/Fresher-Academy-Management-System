package com.example.fams.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.entities.FamsClass;
import com.example.fams.repository.ClassRepository;
import com.example.fams.repository.ClassUserRepository;
import com.example.fams.repository.TrainingProgramRepository;
import com.example.fams.repository.UserClassRepository;
import com.example.fams.repository.UserRepository;
import com.example.fams.services.impl.ClassServiceImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.mockito.junit.jupiter.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
@ExtendWith(MockitoExtension.class)
public class ClassServiceImplTest{

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
    verify(classRepository, times(1)).findAllByStatusIsTrue(pageable);
    verify(classRepository, times(1)).countAllByStatusIsTrue();
    verify(genericConverter, times(entities.size())).toDTO(any(FamsClass.class),
        eq(ClassDTO.class));


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

}
