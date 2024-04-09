package com.example.fams.service;

import com.example.fams.config.CustomValidationException;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ContentDTO;
import com.example.fams.dto.MaterialDTO;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.entities.Content;
import com.example.fams.entities.Material;
import com.example.fams.entities.enums.DeliveryType;
import com.example.fams.entities.enums.TrainingFormat;
import com.example.fams.repository.*;
import com.example.fams.services.impl.ContentServiceImpl;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
  private Authentication authentication;
  @Mock
  private OutputStandardRepository outputStandardRepository;
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
    when(genericConverter.toEntity(any(ContentDTO.class), eq(Content.class)))
            .thenReturn(new Content());

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

//  @Test
//  void testUpdateContent_returnSuccess() {
//    Content content = new Content();
//    content.setId(1L);
//    ContentDTO contentDTO = new ContentDTO();
//    // Giả mạo các phương thức cần thiết
//    Mockito.lenient().when(contentRepository.findById(anyLong())).thenReturn(content);
//    when(contentRepository.save(any())).thenReturn(new Content());
//    when(genericConverter.toDTO(any(Content.class), eq(ContentDTO.class)))
//        .thenReturn(contentDTO);
//
//    // Kiểm thử chức năng cập nhật
//    ResponseEntity<?> responseEntity = contentService.save(contentDTO);
//    ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
//
//    // Kiểm tra phản hồi
//    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    assertEquals("Saved successfully", responseDTO.getDetails().get(0));
//  }
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
  @Test
  public void testSearchSortFilterByDeliveryType_ReturnSuccess() {
    DeliveryType expectedDeliveryTypes = DeliveryType.Assignment_Lab;
    TrainingFormat expectedTrainingFormat = null;
    Long expectedDuration=null;
    int page = 0;
    int limit = 10;
    Pageable pageable = PageRequest.of(page, limit);
    List<Content> expectedContent=new ArrayList<>();
    when(contentRepository.searchSortFilter(expectedDeliveryTypes,expectedTrainingFormat,expectedDuration,pageable)).thenReturn(expectedContent);
    when(contentRepository.countSearchSortFilter(expectedDeliveryTypes,expectedTrainingFormat,expectedDuration)).thenReturn(1L); // Expected count
    ContentDTO searchDTO = new ContentDTO();
    searchDTO.setDeliveryType(expectedDeliveryTypes);
    ResponseEntity<?> response = contentService.searchSortFilter(searchDTO, page + 1, limit);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
    verify(contentRepository, times(1)).searchSortFilter(eq(DeliveryType.Assignment_Lab), eq(null),eq(null), any(Pageable.class));
    verify(contentRepository, times(1)).countSearchSortFilter(eq(DeliveryType.Assignment_Lab),eq(null), eq(null));
  }
  @Test void testSearchSortFilterAdminByDeliveryType_ReturnSuccess() {
    DeliveryType expectedDeliveryTypes = DeliveryType.Assignment_Lab;
    TrainingFormat expectedTrainingFormat = null;
    Long expectedDuration=null;
    String sortById = "asc"; // Sorting order (ascending)
    int page = 0;
    int limit = 10;
    Pageable pageable = PageRequest.of(page, limit);
    List<Content> expectedContent=new ArrayList<>();
    when(contentRepository.searchSortFilterADMIN(expectedDeliveryTypes,expectedTrainingFormat,expectedDuration,sortById,pageable)).thenReturn(expectedContent);
    when(contentRepository.countSearchSortFilter(expectedDeliveryTypes,expectedTrainingFormat,expectedDuration)).thenReturn(1L); // Expected count
    ContentDTO searchDTO = new ContentDTO();
    searchDTO.setDeliveryType(expectedDeliveryTypes);
    ResponseEntity<?> response=contentService.searchSortFilterADMIN(searchDTO,sortById,page+1,limit);
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
    verify(contentRepository, times(1)).searchSortFilterADMIN(eq(DeliveryType.Assignment_Lab), eq(null),eq(null),eq(sortById), any(Pageable.class));
    verify(contentRepository, times(1)).countSearchSortFilter(eq(DeliveryType.Assignment_Lab),eq(null), eq(null));
  }
  @Test
  void testFindAll_Success(){
    // Mock page and limit
    int page = 1;
    int limit = 10;
    Pageable pageable = PageRequest.of(page - 1, limit);

    // Mock data for the repository to return
    List<Content> entities = new ArrayList<>();
    entities.add(new Content());
    entities.add(new Content());
    when(contentRepository.findAllBy(pageable)).thenReturn(entities);
    when(contentRepository.countAllByStatusIsTrue()).thenReturn(2L);

    // Mock conversion from entity to DTO
    when(genericConverter.toDTO(any(Content.class), eq(ContentDTO.class))).thenReturn(new ContentDTO());

    // Call the findAll method
    ResponseEntity<?> response = contentService.findAll(page, limit);

    // Verify the response
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResponseDTO responseDTO = (ResponseDTO) response.getBody();
    assert responseDTO != null;
    List<?> content = (List<?>) responseDTO.getContent();
    assertEquals(2, content.size());

    // Verify method invocations
    verify(contentRepository, times(1)).findAllBy(pageable);
    verify(contentRepository, times(1)).countAllByStatusIsTrue();
    verify(genericConverter, times(entities.size())).toDTO(any(Content.class), eq(ContentDTO.class));
  }
  @Test
  void testCheckExist_ExistingContent() {
    // Mock data
    Long contentId = 1L;
    Content content = new Content();
    when(contentRepository.findById(contentId)).thenReturn(content);
    // Call the method to test
    boolean exists = contentService.checkExist(contentId);
    // Assert
    assertTrue(exists);
  }
  @Test
  void testUpdateContent_Success() {
    // Mock the Authentication object
    Authentication authentication = Mockito.mock(Authentication.class);
    // Mock the getName() method to return a dummy username
    Mockito.when(authentication.getName()).thenReturn("testUser");
    // Set the mock Authentication object to the SecurityContext
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    // Mock data
    Long contentId = 1L;
    ContentDTO contentDTO = new ContentDTO();
    contentDTO.setId(contentId);
    Content content = new Content();
    content.setId(contentId);
    // Mock repository behaviors
    when(contentRepository.findById(contentId)).thenReturn(content);
    when(genericConverter.toDTO(any(Content.class), eq(ContentDTO.class))).thenReturn(contentDTO);
    when(genericConverter.toEntity(any(ContentDTO.class), eq(Content.class))).thenReturn(content);
    // Call the update method
    ResponseEntity<?> responseEntity = contentService.save(contentDTO);
    // Verify results
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
    assertNotNull(responseDTO);
    assertEquals("Saved successfully", responseDTO.getDetails().get(0));

    SecurityContextHolder.clearContext();
  }
}
