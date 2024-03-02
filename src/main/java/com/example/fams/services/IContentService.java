package com.example.fams.services;

import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.ContentDTO;
import org.springframework.http.ResponseEntity;

public interface IContentService extends IGenericService<ContentDTO>{
  ResponseEntity<?> searchSortFilter(ContentDTO contentDTO,
      int page, int limit);

  ResponseEntity<?> searchSortFilterADMIN(ContentDTO contentDTO,
      String sortById,
      int page, int limit);
}
