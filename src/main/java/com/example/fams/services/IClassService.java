package com.example.fams.services;

import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.LearningObjectiveDTO;
import org.springframework.http.ResponseEntity;

public interface IClassService extends IGenericService<ClassDTO>{
  ResponseEntity<?> searchSortFilter(ClassDTO classDTO,
      int page, int limit);

  ResponseEntity<?> searchSortFilterADMIN(ClassDTO classDTO,
      String sortById,
      int page, int limit);
}
