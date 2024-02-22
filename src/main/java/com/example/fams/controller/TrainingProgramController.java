package com.example.fams.controller;

import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.services.IGenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TrainingProgramController {

    @Autowired
    @Qualifier("TrainingProgramService")
    private IGenericService<TrainingProgramDTO> trainingProgramService;

    @GetMapping("user/trainingProgram/findAllByStatusTrue")
    public ResponseEntity<?> getAllSyllabusByStatusTrue(@RequestParam int page, @RequestParam int limit) {
        return trainingProgramService.findAllByStatusTrue(page, limit);
    }
    @GetMapping("admin/trainingProgram/findAll")
    public ResponseEntity<?> getAllTrainingProgram(@RequestParam int page, @RequestParam int limit) {
        return trainingProgramService.findAll(page, limit);
    }
    @PostMapping("admin/trainingProgram/create")
    public ResponseEntity<?> createTrainingProgram(@RequestBody TrainingProgramDTO trainingProgram) {
        return trainingProgramService.save(trainingProgram);
    }
    @PutMapping("admin/trainingProgram/update")
    public ResponseEntity<?> updateTrainingProgram(@RequestBody TrainingProgramDTO trainingProgram) {
        return trainingProgramService.save(trainingProgram);
    }
    @DeleteMapping("admin/trainingProgram/delete/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return trainingProgramService.changeStatus(id);
    }
}
