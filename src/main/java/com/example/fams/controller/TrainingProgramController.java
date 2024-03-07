package com.example.fams.controller;

import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.services.ITrainingProgramService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TrainingProgramController {
    @Autowired
    @Qualifier("TrainingProgramService")
    private ITrainingProgramService trainingProgramService;

    @GetMapping("user/trainingProgram/{id}")
    public ResponseEntity<?> getByTrainingProgramId(@PathVariable Long id) {
        return trainingProgramService.findById(id);
    }

    @GetMapping("user/trainingProgram")
    public ResponseEntity<?> getAllTrainingProgramByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int limit) {
        return trainingProgramService.findAllByStatusTrue(page, limit);
    }
    @GetMapping("admin/trainingProgram")
    public ResponseEntity<?> getAllTrainingProgram(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int limit) {
        return trainingProgramService.findAll(page, limit);
    }
    @GetMapping("user/trainingProgram/search")
    public ResponseEntity<?> searchTrainingProgram(@RequestBody TrainingProgramDTO trainingProgramDTO,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int limit){
        return trainingProgramService.searchSortFilter(trainingProgramDTO, page, limit);
    }
    @GetMapping("admin/trainingProgram/search")
    public ResponseEntity<?> searchTrainingProgramADMIN(@RequestBody TrainingProgramDTO trainingProgramDTO,
                                                        @RequestParam(required = false) String sortById,
                                                        @RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int limit){
        return trainingProgramService.searchSortFilterADMIN(trainingProgramDTO, sortById, page, limit);
    }
    @PostMapping("admin/trainingProgram")
    public ResponseEntity<?> createTrainingProgram(@Valid @RequestBody TrainingProgramDTO trainingProgramDTO) {
        return trainingProgramService.save(trainingProgramDTO);
    }
    @PutMapping("admin/trainingProgram")
    public ResponseEntity<?> updateTrainingProgram(@Valid @RequestBody TrainingProgramDTO trainingProgramDTO){
        return trainingProgramService.save(trainingProgramDTO);
    }
    @DeleteMapping("admin/trainingProgram/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id){
        return trainingProgramService.changeStatus(id);
    }
}
