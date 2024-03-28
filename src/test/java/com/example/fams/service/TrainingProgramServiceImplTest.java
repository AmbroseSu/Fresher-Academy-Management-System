package com.example.fams.service;

import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.entities.TrainingProgram;
import com.example.fams.repository.SyllabusTrainingProgramRepository;
import com.example.fams.repository.TrainingProgramRepository;
import com.example.fams.repository.*;
import com.example.fams.services.impl.TrainingProgramServiceImpl;

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingProgramServiceImplTest {
    @Mock
    private TrainingProgramRepository trainingProgramRepository;
    @Mock
    private GenericConverter genericConverter;
    @Mock
    private SyllabusTrainingProgramRepository syllabusTrainingProgramRepository;
    @InjectMocks
    private TrainingProgramServiceImpl trainingProgramService;
    @Test
    void findAllByStatusTrue_Success(){
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        List<TrainingProgram> entities = new ArrayList();
        entities.add(new TrainingProgram());
        entities.add(new TrainingProgram());


        when(trainingProgramRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
        when(trainingProgramRepository.countAllByStatusIsTrue()).thenReturn(2L);
        when(genericConverter.toDTO(any(TrainingProgram.class), eq(TrainingProgramDTO.class)))
                .thenReturn(new TrainingProgramDTO());


        ResponseEntity<?> response = trainingProgramService.findAllByStatusTrue(page, limit);
        ResponseDTO responseDTO =(ResponseDTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseDTO != null;
        assertEquals(2, ((List<?>) responseDTO.getContent()).size());
        verify(trainingProgramRepository, times(1)).findAllByStatusIsTrue(pageable);
        verify(trainingProgramRepository, times(1)).countAllByStatusIsTrue();
//        verify(genericConverter, times(entities.size())).toDTO(any(TrainingProgram.class),
//                eq(TrainingProgramDTO.class));
    }

    @Test
    void findAllByStatusTrue_EmptyResult() {
        // Arrange
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        // * Tạo mock data thay thế database
        List<TrainingProgram> entities = new ArrayList<>();

        // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
        when(trainingProgramRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
        when(trainingProgramRepository.countAllByStatusIsTrue()).thenReturn(0L);

        // * Test hàm trong service
        ResponseEntity<?> response = trainingProgramService.findAllByStatusTrue(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseDTO != null;
        assertEquals(0, ((List<?>) responseDTO.getContent()).size());
        verify(trainingProgramRepository, times(1)).findAllByStatusIsTrue(pageable);
        verify(trainingProgramRepository, times(1)).countAllByStatusIsTrue();
    }

    @Test
    void testFindById_ExistingTrainingProgram() {
        // * Tạo mock data thay thế database
        Long id = 1L;
        TrainingProgram trainingProgram = new TrainingProgram();
        trainingProgram.setId(id);
        trainingProgram.setStatus(true);

        TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
        trainingProgramDTO.setId(id);

        // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
        when(trainingProgramRepository.findByStatusIsTrueAndId(id)).thenReturn(trainingProgram);
        when(genericConverter.toDTO(trainingProgram, TrainingProgramDTO.class)).thenReturn(trainingProgramDTO);

        // * Test hàm trong service
        ResponseEntity<?> response = trainingProgramService.findById(id);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        TrainingProgramDTO result = (TrainingProgramDTO) responseDTO.getContent();

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertEquals(id, result.getId());
    }

    @Test
    void testFindById_NonExistingTrainingProgram() {
        // Arrange
        Long id = 3L;
        when(trainingProgramRepository.findByStatusIsTrueAndId(id)).thenReturn(null);

        // * Test hàm trong service
        ResponseEntity<?> response = trainingProgramService.findById(id);

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Training program not found with ID: " + id, response.getBody());
    }





}


