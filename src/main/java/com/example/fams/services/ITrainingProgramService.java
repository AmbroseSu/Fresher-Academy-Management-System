package com.example.fams.services;

import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.dto.request.DeleteReplace;
import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface ITrainingProgramService   {
    ResponseEntity<?> findAllByStatusTrue(int page, int limit);

    ResponseEntity<?> findAll(int page, int limit);

    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> save(TrainingProgramDTO trainingProgramDTO);

    ResponseEntity<?> changeStatus(Long id);

    Boolean checkEixst(Long id);
    ResponseEntity<?> searchSortFilter(TrainingProgramDTO trainingProgramDTO,
                                       int page, int limit);

    ResponseEntity<?> searchSortFilterADMIN(TrainingProgramDTO trainingProgramDTO,
                                            String sortById,
                                            int page, int limit);
    List<TrainingProgramDTO> parseExcelFile(MultipartFile file) throws IOException;

    ResponseEntity<?> checkCsvFile(MultipartFile file) throws IOException;

    List<TrainingProgramDTO> parseCsvFile(MultipartFile file) throws IOException;


    //ResponseEntity<?> checkSyllabus(MultipartFile file, Boolean name, Boolean code) throws IOException;
    ResponseEntity<?> checkTrainingProgramReplace(MultipartFile file, Boolean id, Boolean name) throws IOException;
    ResponseEntity<?> checkTrainingProgramSkip(MultipartFile file, Boolean id, Boolean name) throws IOException;
    ResponseEntity<?> changeStatusforUpload(DeleteReplace ids, @RequestParam Boolean id, @RequestParam Boolean name);


}