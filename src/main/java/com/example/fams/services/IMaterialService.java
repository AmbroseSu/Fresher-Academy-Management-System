package com.example.fams.services;

import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.MaterialDTO;
import org.springframework.http.ResponseEntity;

public interface IMaterialService extends IGenericService<MaterialDTO>{
    ResponseEntity<?> searchSortFilter(MaterialDTO materialDTO,
                                       int page, int limit);

    ResponseEntity<?> searchSortFilterADMIN(MaterialDTO materialDTO,
                                            String sortById,
                                            int page, int limit);
}
