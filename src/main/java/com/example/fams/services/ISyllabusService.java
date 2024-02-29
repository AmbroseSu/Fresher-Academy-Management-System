package com.example.fams.services;

import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.SyllabusDTO;
import com.example.fams.dto.TrainingProgramDTO;
import org.springframework.http.ResponseEntity;

public interface ISyllabusService {
    ResponseEntity<?> findAllByStatusTrue(int page, int limit);

    ResponseEntity<?> findAll(int page, int limit);

    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> save(SyllabusDTO syllabusDTO);

    ResponseEntity<?> changeStatus(Long id);

    ResponseEntity<?> searchSortFilter(SyllabusDTO syllabusDTO,
                                       int page, int limit);

    ResponseEntity<?> searchSortFilterADMIN(SyllabusDTO syllabusDTO,
                                            String sortById,
                                            int page, int limit);
}
