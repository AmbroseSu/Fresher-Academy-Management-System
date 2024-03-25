package com.example.fams.services;

import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.TrainingProgramDTO;
import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ITrainingProgramService   {
    ResponseEntity<?> findAllByStatusTrue(int page, int limit);

    ResponseEntity<?> findAll(int page, int limit);

    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> save(TrainingProgramDTO trainingProgramDTO);

    ResponseEntity<?> changeStatus(Long id);

    Boolean checkEixst(Long id);
    ResponseEntity<?> searchSortFilter(TrainingProgramDTO trainingProgramDTO,
                                            String sortByCreatedDate,
                                            int page, int limit);

    ResponseEntity<?> searchSortFilterADMIN(TrainingProgramDTO trainingProgramDTO,
                                            String sortById,
                                            int page, int limit);
    List<TrainingProgramDTO> parseExcelFile(MultipartFile file) throws IOException;

}