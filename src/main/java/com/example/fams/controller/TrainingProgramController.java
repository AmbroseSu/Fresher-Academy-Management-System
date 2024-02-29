package com.example.fams.controller;

import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.services.ILearningObjectiveService;
import com.example.fams.services.ITrainingProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TrainingProgramController {
    @Autowired
    @Qualifier("TrainingProgramService")
    private ITrainingProgramService trainingProgramService;

    @GetMapping("user/trainingProgram/findAllByStatusTrue")
    public ResponseEntity<?> getAllTrainingProgramByStatusTrue(@RequestParam int page, @RequestParam int limit) {
        return trainingProgramService.findAllByStatusTrue(page, limit);
    }
    @GetMapping("admin/trainingProgram/findAll")
    public ResponseEntity<?> getAllTrainingProgram(@RequestParam int page, @RequestParam int limit) {
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
                                                        @RequestParam String name,
                                                        @RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int limit){
        return trainingProgramService.searchSortFilterADMIN(trainingProgramDTO, name, page, limit);
    }
    @PostMapping("admin/trainingProgram/create")
    public ResponseEntity<?> createTrainingProgram(@RequestBody TrainingProgramDTO trainingProgramDTO) {
        return trainingProgramService.save(trainingProgramDTO);
    }
    @PutMapping("admin/trainingProgram/update")
    public ResponseEntity<?> updateTrainingProgram(@RequestBody TrainingProgramDTO trainingProgramDTO){
        return trainingProgramService.save(trainingProgramDTO);
    }
    @DeleteMapping("admin/trainingProgram/delete{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id){
        return trainingProgramService.changeStatus(id);
    }
}
