package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.services.ITrainingProgramService;
import jakarta.validation.Valid;
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
    @GetMapping("/trainingProgram/search")
    public ResponseEntity<?> searchTrainingProgram(@RequestBody TrainingProgramDTO trainingProgramDTO,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int limit){
        return trainingProgramService.searchSortFilter(trainingProgramDTO, page, limit);
    }
    @PreAuthorize("hasAuthority('trainingProgram:Full_Access')")
    @GetMapping("/trainingProgram/search/hidden")
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


    @PreAuthorize("hasAuthority('trainingProgram:Full_Access') || hasAuthority('trainingProgram:Modify') || hasAuthority('trainingProgram:Create')")
    @PostMapping("admin/trainingProgram/upload")
    public /*@ResponseBody*/ ResponseEntity<?> uploadFile(/*@RequestParam("file")*/@RequestBody MultipartFile file) {


        try {
            List<TrainingProgramDTO> trainingProgramDTOS = trainingProgramService.parseExcelFile(file);
            for(TrainingProgramDTO trainingProgramDTO : trainingProgramDTOS) {
                trainingProgramService.save(trainingProgramDTO);
            }
            return ResponseUtil.getObject(trainingProgramDTOS,HttpStatus.CREATED,"Upload Successfully!");
        } catch (Exception e) {
            String result = e.getMessage().toString();
            return ResponseUtil.error(result, "",HttpStatus.BAD_REQUEST);
        }
    }

}
