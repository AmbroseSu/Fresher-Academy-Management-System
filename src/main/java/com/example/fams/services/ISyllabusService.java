package com.example.fams.services;

import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.SyllabusDTO;
import com.example.fams.dto.TrainingProgramDTO;
import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ISyllabusService extends IGenericService<SyllabusDTO> {
//    ResponseEntity<?> findAllByStatusTrue(int page, int limit);
//
//    ResponseEntity<?> findAll(int page, int limit);
//
//    ResponseEntity<?> findById(Long id);
//
//    Boolean checkExist(Long id);
//
//    ResponseEntity<?> save(SyllabusDTO syllabusDTO);
//
//    ResponseEntity<?> changeStatus(Long id);
//
    ResponseEntity<?> searchSortFilter(SyllabusDTO syllabusDTO,
                                       int page, int limit);

    ResponseEntity<?> searchSortFilterADMIN(SyllabusDTO syllabusDTO,
                                            String sortById,
                                            int page, int limit);
    List<SyllabusDTO> parseExcelFile(MultipartFile file) throws IOException;
}
