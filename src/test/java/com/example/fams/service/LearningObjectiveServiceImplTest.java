package com.example.fams.service;

import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.entities.LearningObjective;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

}
