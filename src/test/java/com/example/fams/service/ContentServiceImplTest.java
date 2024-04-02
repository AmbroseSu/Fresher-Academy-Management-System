package com.example.fams.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.fams.config.CustomValidationException;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.ContentDTO;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.entities.Content;
import com.example.fams.entities.FamsClass;
import com.example.fams.entities.enums.DeliveryType;
import com.example.fams.entities.enums.TrainingFormat;
import com.example.fams.repository.ContentLearningObjectiveRepository;
import com.example.fams.repository.ContentRepository;
import com.example.fams.repository.LearningObjectiveContentRepository;
import com.example.fams.repository.LearningObjectiveRepository;
import com.example.fams.repository.UnitRepository;
import com.example.fams.services.impl.ClassServiceImpl;
import com.example.fams.services.impl.ContentServiceImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ContentServiceImplTest {

  @Mock
  private ContentRepository contentRepository;
  @Mock
  private UnitRepository unitRepository;
  @Mock
  private LearningObjectiveContentRepository learningObjectiveContentRepository;
  @Mock
  private ContentLearningObjectiveRepository contentLearningObjectiveRepository;
  @Mock
  private LearningObjectiveRepository learningObjectiveRepository;
  @Mock
  private GenericConverter genericConverter;
  @InjectMocks
  private ContentServiceImpl contentService;

  @Test
  void findAllByStatusTrue_Success(){
    int page = 1;
    int limit = 10;
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Content> entities = new ArrayList<>();
    entities.add(new Content());
    entities.add(new Content());

    when(contentRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
    when(contentRepository.countAllByStatusIsTrue()).thenReturn(2L);
    when(genericConverter.toDTO(any(Content.class), eq(ContentDTO.class)))
        .thenReturn(new ContentDTO());
    ResponseEntity<?> response = contentService.findAllByStatusTrue(page, limit);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assert responseDTO != null;
    assertEquals(2, ((List<?>) responseDTO.getContent()).size());
    verify(contentRepository, times(1)).findAllByStatusIsTrue(pageable);
    verify(contentRepository, times(1)).countAllByStatusIsTrue();
    verify(genericConverter, times(entities.size())).toDTO(any(Content.class),
        eq(ContentDTO.class));

  }


  @Test
  void findAllByStatusTrue_EmptyResult() {
    // Arrange
    int page = 1;
    int limit = 10;
    Pageable pageable = PageRequest.of(page - 1, limit);

    // * Tạo mock data thay thế database
    List<Content> entities = new ArrayList<>();

    // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
    when(contentRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
    when(contentRepository.countAllByStatusIsTrue()).thenReturn(0L);

    // * Test hàm trong service
    ResponseEntity<?> response = contentService.findAllByStatusTrue(page, limit);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();

    // * Kiểm tra kết quả trả về
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assert responseDTO != null;
    assertEquals(0, ((List<?>) responseDTO.getContent()).size());
    verify(contentRepository, times(1)).findAllByStatusIsTrue(pageable);
    verify(contentRepository, times(1)).countAllByStatusIsTrue();
  }


  @Test
  void testFindById_ExistingContent() {
    // * Tạo mock data thay thế database
    Long id = 1L;
    Content content = new Content();
    content.setId(id);
    content.setStatus(true);

    ContentDTO contentDTO = new ContentDTO();
    contentDTO.setId(id);

    // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
    when(contentRepository.findByStatusIsTrueAndId(id)).thenReturn(content);
    when(genericConverter.toDTO(content, ContentDTO.class)).thenReturn(contentDTO);

    // * Test hàm trong service
    ResponseEntity<?> response = contentService.findById(id);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();
    ContentDTO result = (ContentDTO) responseDTO.getContent();

    // * Kiểm tra kết quả trả về
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
    assertEquals(id, result.getId());
  }

  @Test
  void testFindById_NonExistingContent() {
    // Arrange
    Long id = 1L;
    when(contentRepository.findByStatusIsTrueAndId(id)).thenReturn(null);

    // * Test hàm trong service
    ResponseEntity<?> response = contentService.findById(id);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();

    // * Kiểm tra kết quả trả về
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Content not found", responseDTO.getDetails().get(0));
  }

  @Test
  void testCreateNewContent_returnSuccess() {
    Content content = new Content();
    // Arrange
    content.setDeliveryType(DeliveryType.Assignment_Lab);
    content.setTrainingFormat(TrainingFormat.Online);
    content.setDuration(42L);
    ContentDTO contentDTO = new ContentDTO();
    // Mock behavior
    when(contentRepository.save(any())).thenReturn(new Content());
    when(genericConverter.toDTO(any(Content.class), eq(ContentDTO.class)))
        .thenReturn(new ContentDTO());

    // Act
    ResponseEntity<?> responseEntity = contentService.save(contentDTO);
    ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
    // Assert
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    // Add more assertions as needed
    assertEquals("Saved successfully", responseDTO.getDetails().get(0));
  }

  @Test
  void testCreateNewContent_ReturnLearningObjectiveIdsNotExist() {
    ContentDTO contentDTO = new ContentDTO();
    contentDTO.setLearningObjectiveIds(Collections.singletonList(1L)); // Assuming syllabusId 1 doesn't exist

    // Using Mockito.lenient() to avoid UnnecessaryStubbingException
    Mockito.lenient().when(learningObjectiveRepository.existsById(1L)).thenReturn(false);

    // Act & Assert
    CustomValidationException exception = assertThrows(CustomValidationException.class,
        () -> contentService.save(contentDTO));
    assertEquals("LearningObjective with id 1 does not exist", exception.getMessage());

  }

  @Test
  void testUpdateContent_returnSuccess() {
    Content content = new Content();
    content.setId(1L);
    ContentDTO contentDTO = new ContentDTO();
    // Giả mạo các phương thức cần thiết
    Mockito.lenient().when(contentRepository.findById(anyLong())).thenReturn(content);
    when(contentRepository.save(any())).thenReturn(new Content());
    when(genericConverter.toDTO(any(Content.class), eq(ContentDTO.class)))
        .thenReturn(contentDTO);

    // Kiểm thử chức năng cập nhật
    ResponseEntity<?> responseEntity = contentService.save(contentDTO);
    ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

    // Kiểm tra phản hồi
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Saved successfully", responseDTO.getDetails().get(0));
  }
  @Test
  void testChangeStatus_ContentNotFound() {
    // Given
    Long id = 1L;
    when(contentRepository.findById(id)).thenReturn(null);

    // When
    ResponseEntity<?> responseEntity = contentService.changeStatus(id);
    ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
    // Then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertEquals("Content not found", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
    assertEquals("Cannot change status of non-existing Content", (responseDTO.getMessage()));
    verify(contentRepository, never()).save(any()); // Ensure save is not called
  }

  @Test
  public void testChangeStatus_StatusChangedSuccessfully() {
    // Given
    Long id = 1L;
    Content entity = new Content();
    entity.setId(id);
    entity.setStatus(true); // Initial status is true
    when(contentRepository.findById(id)).thenReturn(entity);

    // When
    ResponseEntity<?> responseEntity = contentService.changeStatus(id);

    // Then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Status changed successfully", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
    assertEquals(false, entity.getStatus()); // Ensure status is toggled
    verify(contentRepository, times(1)).save(entity); // Verify that save is called once
  }


}
