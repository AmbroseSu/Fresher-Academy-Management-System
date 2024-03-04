package com.example.fams.services;

import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.TrainingProgramDTO;
import org.springframework.http.ResponseEntity;

public interface ITrainingProgramService  {
    ResponseEntity<?> findAllByStatusTrue(int page, int limit);

    ResponseEntity<?> findAll(int page, int limit);

    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> save(TrainingProgramDTO trainingProgramDTO);

    ResponseEntity<?> changeStatus(Long id);

    ResponseEntity<?> searchSortFilter(TrainingProgramDTO trainingProgramDTO,
                                       int page, int limit);

    ResponseEntity<?> searchSortFilterADMIN(TrainingProgramDTO trainingProgramDTO,
                                            String sortById,
                                            int page, int limit);

}