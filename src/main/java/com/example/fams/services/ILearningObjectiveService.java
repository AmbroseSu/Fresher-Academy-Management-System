package com.example.fams.services;

import com.example.fams.dto.LearningObjectiveDTO;
import org.springframework.http.ResponseEntity;

public interface ILearningObjectiveService extends IGenericService<LearningObjectiveDTO>{
    ResponseEntity<?> searchSortFilter(LearningObjectiveDTO learningObjectiveDTO,
                                       int page, int limit);

    ResponseEntity<?> searchSortFilterADMIN(LearningObjectiveDTO learningObjectiveDTO,
                                                  String sortById,
                                                  int page, int limit);
}
