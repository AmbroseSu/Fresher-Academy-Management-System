package com.example.fams.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.ContentDTO;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.entities.Content;
import com.example.fams.entities.FamsClass;
import com.example.fams.repository.ContentLearningObjectiveRepository;
import com.example.fams.repository.ContentRepository;
import com.example.fams.repository.LearningObjectiveContentRepository;
import com.example.fams.repository.LearningObjectiveRepository;
import com.example.fams.repository.UnitRepository;
import com.example.fams.services.impl.ClassServiceImpl;
import com.example.fams.services.impl.ContentServiceImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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


}
