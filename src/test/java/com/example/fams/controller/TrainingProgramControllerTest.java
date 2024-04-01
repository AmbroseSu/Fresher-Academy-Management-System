package com.example.fams.controller;

import com.example.fams.dto.ResponseDTO;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.services.impl.TrainingProgramServiceImpl;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class TrainingProgramControllerTest {

    @Mock
    TrainingProgramServiceImpl trainingProgramService;
    @InjectMocks
    TrainingProgramController trainingProgramController;
    @Test
    void testUpdateTrainingProgram_returnTrainingProgramIdNotExists() {
        Long id = 1L;
        TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
        trainingProgramDTO.setId(id);

        when(trainingProgramService.checkEixst(id)).thenReturn(false);

        ResponseEntity<?> responseEntity = trainingProgramController.updateTrainingProgram(trainingProgramDTO, id);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("TrainingProgram not exist", responseDTO.getMessage());


    }
}
