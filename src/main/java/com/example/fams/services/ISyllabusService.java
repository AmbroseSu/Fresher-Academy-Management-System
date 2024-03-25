package com.example.fams.services;

import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.SyllabusDTO;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.dto.request.DeleteReplaceSyllabus;
import com.example.fams.entities.enums.SyllabusDuplicateHandle;
import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
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
                                       String sortByCreatedDate,
                                       int page, int limit);

    ResponseEntity<?> searchSortFilterADMIN(SyllabusDTO syllabusDTO,
                                            String sortById,
                                            int page, int limit);
    List<SyllabusDTO> parseExcelFile(MultipartFile file) throws IOException;

    ResponseEntity<?> checkCsvFile(MultipartFile file) throws IOException;

    List<SyllabusDTO> parseCsvFile(MultipartFile file) throws IOException;


    //ResponseEntity<?> checkSyllabus(MultipartFile file, Boolean name, Boolean code) throws IOException;
    ResponseEntity<?> checkSyllabusReplace(MultipartFile file, Boolean name, Boolean code) throws IOException;
    ResponseEntity<?> checkSyllabusSkip(MultipartFile file, Boolean name, Boolean code) throws IOException;
    ResponseEntity<?> changeStatusforUpload(DeleteReplaceSyllabus ids, @RequestParam Boolean name, @RequestParam Boolean code);
}
