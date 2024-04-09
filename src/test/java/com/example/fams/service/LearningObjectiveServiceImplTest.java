package com.example.fams.service;

import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.entities.*;
import com.example.fams.repository.*;
import com.example.fams.services.impl.LearningObjectiveServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningObjectiveServiceImplTest {

    @Mock
    private LearningObjectiveRepository learningObjectiveRepository;
    @Mock
    private LearningObjectiveContentRepository learningObjectiveContentRepository;
    @Mock
    private SyllabusObjectiveRepository syllabusObjectiveRepository;
    @Mock
    private ContentRepository contentRepository;
    @Mock
    private SyllabusRepository syllabusRepository;
    @Mock
    private GenericConverter genericConverter;

    @InjectMocks
    private LearningObjectiveServiceImpl learningObjectiveService;

    @Test
    void findAllByStatusTrue_Success() {
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        // * Tạo mock data thay thế database
        List<LearningObjective> entities = new ArrayList<>();
        entities.add(new LearningObjective());
        entities.add(new LearningObjective());

        // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
        when(learningObjectiveRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
        when(learningObjectiveRepository.countAllByStatusIsTrue()).thenReturn(2L);
        when(genericConverter.toDTO(any(LearningObjective.class), eq(LearningObjectiveDTO.class)))
                .thenReturn(new LearningObjectiveDTO());

        // * Test hàm trong service
        ResponseEntity<?> response = learningObjectiveService.findAllByStatusTrue(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseDTO != null;
        assertEquals(2, ((List<?>) responseDTO.getContent()).size());
        verify(learningObjectiveRepository, times(1)).findAllByStatusIsTrue(pageable);
        verify(learningObjectiveRepository, times(1)).countAllByStatusIsTrue();
        verify(genericConverter, times(entities.size())).toDTO(any(LearningObjective.class),
                eq(LearningObjectiveDTO.class));
    }

    @Test
    void findAllByStatusTrue_EmptyResult() {
        // Arrange
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        // * Tạo mock data thay thế database
        List<LearningObjective> entities = new ArrayList<>();

        // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
        when(learningObjectiveRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
        when(learningObjectiveRepository.countAllByStatusIsTrue()).thenReturn(0L);

        // * Test hàm trong service
        ResponseEntity<?> response = learningObjectiveService.findAllByStatusTrue(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseDTO != null;
        assertEquals(0, ((List<?>) responseDTO.getContent()).size());
        verify(learningObjectiveRepository, times(1)).findAllByStatusIsTrue(pageable);
        verify(learningObjectiveRepository, times(1)).countAllByStatusIsTrue();
    }

    @Test
    void testFindById_ExistingLearningObjective() {
        // * Tạo mock data thay thế database
        Long id = 1L;
        LearningObjective learningObjective = new LearningObjective();
        learningObjective.setId(id);
        learningObjective.setStatus(true);

        LearningObjectiveDTO learningObjectiveDTO = new LearningObjectiveDTO();
        learningObjectiveDTO.setId(id);

        // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
        when(learningObjectiveRepository.findByStatusIsTrueAndId(id)).thenReturn(learningObjective);
        when(genericConverter.toDTO(learningObjective, LearningObjectiveDTO.class)).thenReturn(learningObjectiveDTO);

        // * Test hàm trong service
        ResponseEntity<?> response = learningObjectiveService.findById(id);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        LearningObjectiveDTO result = (LearningObjectiveDTO) responseDTO.getContent();

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertEquals(id, result.getId());
    }

    @Test
    void testFindById_NonExistingLearningObjective() {
        // Arrange
        Long id = 1L;
        when(learningObjectiveRepository.findByStatusIsTrueAndId(id)).thenReturn(null);

        // * Test hàm trong service
        ResponseEntity<?> response = learningObjectiveService.findById(id);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Learning Objective not found", responseDTO.getDetails().get(0));
    }


    @Test
    void testSave_CreateNewLearningObjective() {
        // Arrange
        LearningObjectiveDTO learningObjectiveDTO = new LearningObjectiveDTO();
        learningObjectiveDTO.setCode("LO001");
        learningObjectiveDTO.setName("Learning Objective 1");
        learningObjectiveDTO.setType(1);
        learningObjectiveDTO.setDescription("This is a learning objective");
        learningObjectiveDTO.setStatus(true);


        LearningObjective savedLearningObjective = new LearningObjective();
        savedLearningObjective.setId(1L);

        when(learningObjectiveRepository.save(any(LearningObjective.class))).thenReturn(savedLearningObjective);
        when(genericConverter.toDTO(any(LearningObjective.class), eq(LearningObjectiveDTO.class))).thenReturn(learningObjectiveDTO);

        // Act
        ResponseEntity<?> response = learningObjectiveService.save(learningObjectiveDTO);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
 }

//    @Test
//    void testSave_UpdateExistingLearningObjective() {
//        // Arrange
//        Long learningObjectiveId = 1L;
//        LearningObjectiveDTO learningObjectiveDTO = new LearningObjectiveDTO();
//        learningObjectiveDTO.setId(learningObjectiveId);
//        learningObjectiveDTO.setCode("LO001");
//        learningObjectiveDTO.setName("Learning Objective 1");
//        learningObjectiveDTO.setType(1);
//        learningObjectiveDTO.setDescription("This is a learning objective");
//        learningObjectiveDTO.setStatus(true);
//
//
//        LearningObjective existingLearningObjective = new LearningObjective();
//        existingLearningObjective.setId(learningObjectiveId);
//
//        LearningObjective updatedLearningObjective = new LearningObjective();
//        updatedLearningObjective.setId(learningObjectiveId);
//
//        when(learningObjectiveRepository.findById(learningObjectiveId)).thenReturn(existingLearningObjective);
//        when(learningObjectiveRepository.save(any(LearningObjective.class))).thenReturn(updatedLearningObjective);
//               when(genericConverter.toDTO(any(LearningObjective.class), eq(LearningObjectiveDTO.class))).thenReturn(learningObjectiveDTO);
//
//        // Act
//        ResponseEntity<?> response = learningObjectiveService.save(learningObjectiveDTO);
//        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
//            }

    @Test
    void testCheckExist_ExistingLearningObjective() {
        // Arrange
        Long learningObjectiveId = 1L;
        LearningObjective existingLearningObjective = new LearningObjective();
        existingLearningObjective.setId(learningObjectiveId);

        when(learningObjectiveRepository.findById(learningObjectiveId)).thenReturn(existingLearningObjective);

        // Act
        Boolean result = learningObjectiveService.checkExist(learningObjectiveId);

        // Assert
        assertTrue(result);
        verify(learningObjectiveRepository, times(1)).findById(learningObjectiveId);
    }

    @Test
    void testCheckExist_NonExistingLearningObjective() {
        // Arrange
        Long learningObjectiveId = 1L;
        when(learningObjectiveRepository.findById(learningObjectiveId)).thenReturn(null);

        // Act
        Boolean result = learningObjectiveService.checkExist(learningObjectiveId);

        // Assert
        assertFalse(result);
        verify(learningObjectiveRepository, times(1)).findById(learningObjectiveId);
    }

    @Test
    void testFindAll_Success1() {
        // Arrange
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<LearningObjective> entities = new ArrayList<>();
        entities.add(new LearningObjective());
        entities.add(new LearningObjective());
        Long totalCount = 2L;

        when(learningObjectiveRepository.findAllBy(pageable)).thenReturn(entities);
        when(learningObjectiveRepository.countAllByStatusIsTrue()).thenReturn(totalCount);
        when(genericConverter.toDTO(any(LearningObjective.class), eq(LearningObjectiveDTO.class)))
                .thenReturn(new LearningObjectiveDTO());

        // Act
        ResponseEntity<?> response = learningObjectiveService.findAll(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertEquals(entities.size(), ((List<?>) responseDTO.getContent()).size());

        verify(learningObjectiveRepository, times(1)).findAllBy(pageable);
        verify(learningObjectiveRepository, times(1)).countAllByStatusIsTrue();
        verify(genericConverter, times(entities.size())).toDTO(any(LearningObjective.class), eq(LearningObjectiveDTO.class));
    }

    @Test
    void testFindAll_NoResults() {
        // Arrange
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<LearningObjective> entities = new ArrayList<>();
        Long totalCount = 0L;

        when(learningObjectiveRepository.findAllBy(pageable)).thenReturn(entities);
        when(learningObjectiveRepository.countAllByStatusIsTrue()).thenReturn(totalCount);

        // Act
        ResponseEntity<?> response = learningObjectiveService.findAll(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertTrue(((List<?>) responseDTO.getContent()).isEmpty());

        verify(learningObjectiveRepository, times(1)).findAllBy(pageable);
        verify(learningObjectiveRepository, times(1)).countAllByStatusIsTrue();
    }


    @Test
    void testFindAll_Success() {
        // Arrange
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<LearningObjective> entities = new ArrayList<>();
        entities.add(new LearningObjective());
        entities.add(new LearningObjective());
        Long totalCount = 2L;

        when(learningObjectiveRepository.findAllBy(pageable)).thenReturn(entities);
        when(learningObjectiveRepository.countAllByStatusIsTrue()).thenReturn(totalCount);
        when(genericConverter.toDTO(any(LearningObjective.class), eq(LearningObjectiveDTO.class)))
                .thenReturn(new LearningObjectiveDTO());

        // Act
        ResponseEntity<?> response = learningObjectiveService.findAll(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertEquals(entities.size(), ((List<?>) responseDTO.getContent()).size());

        verify(learningObjectiveRepository, times(1)).findAllBy(pageable);
        verify(learningObjectiveRepository, times(1)).countAllByStatusIsTrue();
        verify(genericConverter, times(entities.size())).toDTO(any(LearningObjective.class), eq(LearningObjectiveDTO.class));
    }

    @Test
    void testFindAll_NoResults1() {
        // Arrange
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<LearningObjective> entities = new ArrayList<>();
        Long totalCount = 0L;

        when(learningObjectiveRepository.findAllBy(pageable)).thenReturn(entities);
        when(learningObjectiveRepository.countAllByStatusIsTrue()).thenReturn(totalCount);

        // Act
        ResponseEntity<?> response = learningObjectiveService.findAll(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertTrue(((List<?>) responseDTO.getContent()).isEmpty());

        verify(learningObjectiveRepository, times(1)).findAllBy(pageable);
        verify(learningObjectiveRepository, times(1)).countAllByStatusIsTrue();
    }

    @Test
    void testChangeStatus_ExistingLearningObjective_StatusTrue() {
        // Arrange
        Long id = 1L;
        LearningObjective learningObjective = new LearningObjective();
        learningObjective.setId(id);
        learningObjective.setStatus(true);

        when(learningObjectiveRepository.findById(id)).thenReturn(learningObjective);

        // Act
        ResponseEntity<?> response = learningObjectiveService.changeStatus(id);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Status changed successfully", responseDTO.getDetails().get(0));
        assertFalse(learningObjective.getStatus());
        verify(learningObjectiveRepository, times(1)).findById(id);
        verify(learningObjectiveRepository, times(1)).save(learningObjective);
    }

    @Test
    void testChangeStatus_ExistingLearningObjective_StatusFalse() {
        // Arrange
        Long id = 1L;
        LearningObjective learningObjective = new LearningObjective();
        learningObjective.setId(id);
        learningObjective.setStatus(false);

        when(learningObjectiveRepository.findById(id)).thenReturn(learningObjective);

        // Act
        ResponseEntity<?> response = learningObjectiveService.changeStatus(id);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Status changed successfully", responseDTO.getDetails().get(0));
        assertTrue(learningObjective.getStatus());
        verify(learningObjectiveRepository, times(1)).findById(id);
        verify(learningObjectiveRepository, times(1)).save(learningObjective);
    }

    @Test
    void testSearchSortFilter_Success() {
        // Arrange
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);
        String code = "LO001";
        String name = "Learning Objective 1";
        Integer type = 1;
        String description = "This is a learning objective";
        LearningObjectiveDTO learningObjectiveDTO = new LearningObjectiveDTO();
        learningObjectiveDTO.setCode(code);
        learningObjectiveDTO.setName(name);
        learningObjectiveDTO.setType(type);
        learningObjectiveDTO.setDescription(description);

        List<LearningObjective> entities = new ArrayList<>();
        entities.add(new LearningObjective());
        entities.add(new LearningObjective());
        Long totalCount = 2L;

        when(learningObjectiveRepository.searchSortFilter(code, name, type, description, pageable)).thenReturn(entities);
        when(learningObjectiveRepository.countSearchSortFilter(code, name, type, description)).thenReturn(totalCount);
        when(genericConverter.toDTO(any(LearningObjective.class), eq(LearningObjectiveDTO.class)))
                .thenReturn(new LearningObjectiveDTO());

        // Act
        ResponseEntity<?> response = learningObjectiveService.searchSortFilter(learningObjectiveDTO, page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertEquals(entities.size(), ((List<?>) responseDTO.getContent()).size());

        verify(learningObjectiveRepository, times(1)).searchSortFilter(code, name, type, description, pageable);
        verify(learningObjectiveRepository, times(1)).countSearchSortFilter(code, name, type, description);
        verify(genericConverter, times(entities.size())).toDTO(any(LearningObjective.class), eq(LearningObjectiveDTO.class));
    }

    @Test
    void testSearchSortFilterADMIN_Success() {
        // Arrange
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);
        String code = "LO001";
        String name = "Learning Objective 1";
        Integer type = 1;
        String description = "This is a learning objective";
        String sortById = "id";
        LearningObjectiveDTO learningObjectiveDTO = new LearningObjectiveDTO();
        learningObjectiveDTO.setCode(code);
        learningObjectiveDTO.setName(name);
        learningObjectiveDTO.setType(type);
        learningObjectiveDTO.setDescription(description);

        List<LearningObjective> entities = new ArrayList<>();
        entities.add(new LearningObjective());
        entities.add(new LearningObjective());
        Long totalCount = 2L;

        when(learningObjectiveRepository.searchSortFilterADMIN(code, name, type, description, sortById, pageable)).thenReturn(entities);
        when(learningObjectiveRepository.countSearchSortFilter(code, name, type, description)).thenReturn(totalCount);
        when(genericConverter.toDTO(any(LearningObjective.class), eq(LearningObjectiveDTO.class)))
                .thenReturn(new LearningObjectiveDTO());

        // Act
        ResponseEntity<?> response = learningObjectiveService.searchSortFilterADMIN(learningObjectiveDTO, sortById, page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertEquals(entities.size(), ((List<?>) responseDTO.getContent()).size());

        verify(learningObjectiveRepository, times(1)).searchSortFilterADMIN(code, name, type, description, sortById, pageable);
        verify(learningObjectiveRepository, times(1)).countSearchSortFilter(code, name, type, description);
        verify(genericConverter, times(entities.size())).toDTO(any(LearningObjective.class), eq(LearningObjectiveDTO.class));
    }

    @Test
    void testLoadContentLearningObjectiveFromListContentId() {
        // Arrange
        Long learningObjectiveId = 1L;
        List<Long> requestContentIds = Arrays.asList(1L, 2L, 3L);

        LearningObjective learningObjective = new LearningObjective();
        learningObjective.setId(learningObjectiveId);

        Content content1 = new Content();
        content1.setId(1L);
        Content content2 = new Content();
        content2.setId(2L);
        Content content3 = new Content();
        content3.setId(3L);

        when(learningObjectiveRepository.findById(learningObjectiveId)).thenReturn(learningObjective);
        when(contentRepository.findById(1L)).thenReturn(content1);
        when(contentRepository.findById(2L)).thenReturn(content2);
        when(contentRepository.findById(3L)).thenReturn(content3);

        // Act
        learningObjectiveService.loadContentLearningObjectiveFromListContentId(requestContentIds, learningObjectiveId);

        // Assert
        verify(learningObjectiveRepository, times(3)).findById(learningObjectiveId);
        verify(contentRepository, times(1)).findById(1L);
        verify(contentRepository, times(1)).findById(2L);
        verify(contentRepository, times(1)).findById(3L);
        verify(learningObjectiveContentRepository, times(3)).save(any(LearningObjectiveContent.class));
    }

    @Test
    void testLoadSyllabusObjectiveFromListSyllabusId() {
        // Arrange
        Long learningObjectiveId = 1L;
        List<Long> requestSyllabusIds = Arrays.asList(1L, 2L, 3L);

        LearningObjective learningObjective = new LearningObjective();
        learningObjective.setId(learningObjectiveId);

        Syllabus syllabus1 = new Syllabus();
        syllabus1.setId(1L);
        Syllabus syllabus2 = new Syllabus();
        syllabus2.setId(2L);
        Syllabus syllabus3 = new Syllabus();
        syllabus3.setId(3L);

        when(learningObjectiveRepository.findById(learningObjectiveId)).thenReturn(learningObjective);
        when(syllabusRepository.findById(1L)).thenReturn(java.util.Optional.of(syllabus1));
        when(syllabusRepository.findById(2L)).thenReturn(java.util.Optional.of(syllabus2));
        when(syllabusRepository.findById(3L)).thenReturn(java.util.Optional.of(syllabus3));

        // Act
        learningObjectiveService.loadSyllabusObjectiveFromListSyllabusId(requestSyllabusIds, learningObjectiveId);

        // Assert
        verify(learningObjectiveRepository, times(3)).findById(learningObjectiveId);
        verify(syllabusRepository, times(1)).findById(1L);
        verify(syllabusRepository, times(1)).findById(2L);
        verify(syllabusRepository, times(1)).findById(3L);
        verify(syllabusObjectiveRepository, times(3)).save(any(SyllabusObjective.class));
    }
    @Test
    void testSave_UpdateExistingLearningObjective() {
        // Arrange
        Long learningObjectiveId = 1L;
        LearningObjectiveDTO learningObjectiveDTO = new LearningObjectiveDTO();
        learningObjectiveDTO.setId(learningObjectiveId);
        learningObjectiveDTO.setCode("LO001");
        learningObjectiveDTO.setName("Learning Objective 1");
        learningObjectiveDTO.setType(1);
        learningObjectiveDTO.setDescription("This is a learning objective");
        learningObjectiveDTO.setStatus(true);


        LearningObjective existingLearningObjective = new LearningObjective();
        existingLearningObjective.setId(learningObjectiveId);

        LearningObjective updatedLearningObjective = new LearningObjective();
        updatedLearningObjective.setId(learningObjectiveId);

        User testUser = new User();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(testUser.getUsername());


        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(learningObjectiveRepository.findById(learningObjectiveId)).thenReturn(existingLearningObjective);
        when(learningObjectiveRepository.save(any(LearningObjective.class))).thenReturn(updatedLearningObjective);
        when(genericConverter.toDTO(any(LearningObjective.class), eq(LearningObjectiveDTO.class))).thenReturn(learningObjectiveDTO);

        // Act
        ResponseEntity<?> response = learningObjectiveService.save(learningObjectiveDTO);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
          }
}
