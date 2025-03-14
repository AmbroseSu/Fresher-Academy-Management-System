package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.dto.request.DeleteReplace;
import com.example.fams.entities.enums.DuplicateHandle;
import com.example.fams.services.ITrainingProgramService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class TrainingProgramController {
    @Autowired
    @Qualifier("TrainingProgramService")
    private ITrainingProgramService trainingProgramService;
    @PreAuthorize("hasAuthority('trainingProgram:Full_Access') || hasAuthority('trainingProgram:View')")
    @GetMapping("/trainingProgram/{id}")
    public ResponseEntity<?> getByTrainingProgramId(@PathVariable Long id) {
        return trainingProgramService.findById(id);
    }
    @PreAuthorize("hasAuthority('trainingProgram:Full_Access') || hasAuthority('trainingProgram:View')")
    @GetMapping("/trainingProgram")
    public ResponseEntity<?> getAllTrainingProgramByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int limit) {
        return trainingProgramService.findAllByStatusTrue(page, limit);
    }
    @PreAuthorize("hasAuthority('trainingProgram:Full_Access')")
    @GetMapping("/trainingProgram/hidden")
    public ResponseEntity<?> getAllTrainingProgram(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int limit) {
        return trainingProgramService.findAll(page, limit);
    }
    @PreAuthorize("hasAuthority('trainingProgram:Full_Access') || hasAuthority('trainingProgram:View')")
    @PostMapping("/trainingProgram/search")
    public ResponseEntity<?> searchTrainingProgram(@RequestBody TrainingProgramDTO trainingProgramDTO,
                                                   @RequestParam(required = false) String sortByCreatedDate,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int limit){
        return trainingProgramService.searchSortFilter(trainingProgramDTO, sortByCreatedDate, page, limit);
    }
    @PreAuthorize("hasAuthority('trainingProgram:Full_Access')")
    @PostMapping("/trainingProgram/search/hidden")
    public ResponseEntity<?> searchTrainingProgramADMIN(@RequestBody TrainingProgramDTO trainingProgramDTO,
                                                        @RequestParam(required = false) String sortById,
                                                        @RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int limit){
        return trainingProgramService.searchSortFilterADMIN(trainingProgramDTO, sortById, page, limit);
    }

    @PreAuthorize("hasAuthority('trainingProgram:Full_Access') || hasAuthority('trainingProgram:Create')")
    @PostMapping("/trainingProgram")
    public ResponseEntity<?> createTrainingProgram(@Valid @RequestBody TrainingProgramDTO trainingProgramDTO) {
        return trainingProgramService.save(trainingProgramDTO);
    }

    @PreAuthorize("hasAuthority('trainingProgram:Full_Access') || hasAuthority('trainingProgram:Modify')")
    @PutMapping("/trainingProgram/{id}")
    public ResponseEntity<?> updateTrainingProgram(@Valid @RequestBody TrainingProgramDTO trainingProgramDTO, @PathVariable(name ="id") Long id){

        if(trainingProgramService.checkEixst(id)){
            trainingProgramDTO.setId(id);
          return   trainingProgramService.save(trainingProgramDTO);
        }
        return ResponseUtil.error("Not found","TrainingProgram not exist", HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('trainingProgram:Full_Access')")
    @DeleteMapping("/trainingProgram/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id){
        return trainingProgramService.changeStatus(id);
    }


    @PreAuthorize("hasAuthority('syllabus:Full_Access') || hasAuthority('syllabus:Create')")
    @PostMapping("trainingProgram/updatecsv")
    public ResponseEntity<?> uploadFileReplaceNew(@RequestParam("file") MultipartFile file,
        @RequestParam Boolean id,
        @RequestParam Boolean name,
        @RequestParam DuplicateHandle duplicateHandle
    ) throws IOException {
        //String s = syllabusService.checkCsvFile(file).getStatusCode().toString();

        try {
            if(duplicateHandle.toString().equals("REPLACE")){
                return trainingProgramService.checkTrainingProgramReplace(file, id, name);
            }else{
                if(duplicateHandle.toString().equals("SKIP")){
                    return trainingProgramService.checkTrainingProgramSkip(file, id, name);
                }else{
                    if(duplicateHandle.toString().equals("ALLOW")){

                        if(trainingProgramService.checkCsvFile(file).getStatusCode().toString().equals("200 OK")){
                            List<TrainingProgramDTO> trainingProgramDTOS = trainingProgramService.parseCsvFile(file);
                            for(TrainingProgramDTO trainingProgramDTO : trainingProgramDTOS) {
                                trainingProgramService.save(trainingProgramDTO);
                            }
                            return ResponseUtil.getObject(null, HttpStatus.OK, "Saved successfully");
                        }else{
                            return ResponseUtil.error("Please check format file", "", HttpStatus.BAD_REQUEST);
                        }
                    }
                }
            }

            return ResponseUtil.error("Import False", "Import Syllabus False",HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            String result = e.getMessage().toString();
            return ResponseUtil.error(result, "",HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('syllabus:Full_Access')")
    @DeleteMapping("/trainingProgram/delete")
    public ResponseEntity<?> changeStatusForUpload(@RequestBody DeleteReplace ids,
        @RequestParam(value = "id") Boolean id,
        @RequestParam(value = "name") Boolean name
        ) {

        return trainingProgramService.changeStatusforUpload(ids, id, name);
    }

}
