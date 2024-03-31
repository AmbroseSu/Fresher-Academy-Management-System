package com.example.fams.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.amazonaws.Response;
import com.example.fams.config.CustomValidationException;
import com.example.fams.controller.TrainingProgramController;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.entities.Syllabus;
import com.example.fams.entities.TrainingProgram;
import com.example.fams.repository.SyllabusTrainingProgramRepository;
import com.example.fams.repository.TrainingProgramRepository;
import com.example.fams.repository.*;
import com.example.fams.services.ServiceUtils;
import com.example.fams.services.impl.TrainingProgramServiceImpl;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.aspectj.lang.annotation.Before;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@ExtendWith(MockitoExtension.class)
public class TrainingProgramServiceImplTest {
    @Mock
    private TrainingProgramRepository trainingProgramRepository;
    @Mock
    private GenericConverter genericConverter;
    @Mock
    private SyllabusTrainingProgramRepository syllabusTrainingProgramRepository;
    @Mock
    private SyllabusRepository syllabusRepository;

    @Mock
    private ServiceUtils serviceUtils;
    @InjectMocks
    private TrainingProgramServiceImpl trainingProgramService;


    @Test
    void findAllByStatusTrue_Success() {
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
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

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
//        assertEquals(1,1);
    }

    @Test
    void testFindById_NonExistingTrainingProgram() {
        // Arrange
        Long id = 1L;
        when(trainingProgramRepository.findByStatusIsTrueAndId(id)).thenReturn(null);

        // * Test hàm trong service
        ResponseEntity<?> response = trainingProgramService.findById(id);

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Training program not found with ID: " + id, response.getBody());
    }


    @Test
    void testCreateNewTrainingProgram_returnSuccess() {
        TrainingProgram trainingProgram = new TrainingProgram();
        List<TrainingProgram> entities = new ArrayList();
        entities.add(new TrainingProgram());
        entities.add(new TrainingProgram());
        // Arrange
        trainingProgram.setName("Tuan");
        trainingProgram.setTraining_status(1);
        trainingProgram.setDuration(50L);
        trainingProgram.setStartTime(50L);
        TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
        // Mock behavior
        when(trainingProgramRepository.save(any())).thenReturn(new TrainingProgram());
        when(genericConverter.toDTO(any(TrainingProgram.class), eq(TrainingProgramDTO.class)))
                .thenReturn(new TrainingProgramDTO());

        // Act
        ResponseEntity<?> responseEntity = trainingProgramService.save(trainingProgramDTO);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // Add more assertions as needed
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
    }

    @Test
    void testCreateNewTrainingProgram_ReturnSyllabusIdsNotExist() {
        TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
        trainingProgramDTO.setSyllabusIds(Collections.singletonList(1L)); // Assuming syllabusId 1 doesn't exist

        // Using Mockito.lenient() to avoid UnnecessaryStubbingException
        Mockito.lenient().when(syllabusRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        CustomValidationException exception = assertThrows(CustomValidationException.class,
                () -> trainingProgramService.save(trainingProgramDTO));
        assertEquals("Syllabus with id [1] does not exist", exception.getMessage());

    }

    @Test
    void testUpdateTrainingProgram_returnSuccess() {
        TrainingProgram trainingProgram = new TrainingProgram();
        trainingProgram.setId(1L);

        // Giả mạo các phương thức cần thiết
        Mockito.lenient().when(trainingProgramRepository.findOneById(any())).thenReturn(trainingProgram);
        when(trainingProgramRepository.save(any())).thenReturn(new TrainingProgram());
        when(genericConverter.toDTO(any(TrainingProgram.class), eq(TrainingProgramDTO.class)))
                .thenReturn(new TrainingProgramDTO());

        // Kiểm thử chức năng cập nhật
        ResponseEntity<?> responseEntity = trainingProgramService.save(new TrainingProgramDTO());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        // Kiểm tra phản hồi
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
    }

    @Test
     void testChangeStatus_TrainingProgramNotFound() {
        // Given
        Long id = 1L;
        when(trainingProgramRepository.findOneById(id)).thenReturn(null);

        // When
        ResponseEntity<?> responseEntity = trainingProgramService.changeStatus(id);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        // Then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Training program not found", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
        assertEquals("Cannot change status of non-existing training program", (responseDTO.getMessage()));
        verify(trainingProgramRepository, never()).save(any()); // Ensure save is not called
    }

    @Test
    public void testChangeStatus_StatusChangedSuccessfully() {
        // Given
        Long id = 1L;
        TrainingProgram entity = new TrainingProgram();
        entity.setId(id);
        entity.setStatus(true); // Initial status is true
        when(trainingProgramRepository.findOneById(id)).thenReturn(entity);

        // When
        ResponseEntity<?> responseEntity = trainingProgramService.changeStatus(id);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Status changed successfully", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
        assertEquals(false, entity.getStatus()); // Ensure status is toggled
        verify(trainingProgramRepository, times(1)).save(entity); // Verify that save is called once
    }
}

