package com.example.fams.controller;

import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.entities.LearningObjective;
import com.example.fams.services.IGenericService;
import com.example.fams.services.ILearningObjectiveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class LearningObjectiveController {
    @Autowired
    @Qualifier("LearningObjectiveService")
    private ILearningObjectiveService learningObjectiveService;

    @GetMapping("user/learningObjective/findAllByStatusTrue")
    public ResponseEntity<?> getAllLearningObjectivesByStatusTrue(@RequestParam int page, @RequestParam int limit) {
        return learningObjectiveService.findAllByStatusTrue(page, limit);
    }

    @GetMapping("admin/learningObjective/findAll")
    public ResponseEntity<?> getAllLearningObjectives(@RequestParam int page, @RequestParam int limit) {
        return learningObjectiveService.findAll(page, limit);
    }

    @GetMapping("user/learningObjective/search")
    public ResponseEntity<?> searchLearningObjective(@RequestBody LearningObjectiveDTO learningObjectiveDTO,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit){
        return learningObjectiveService.searchSortFilter(learningObjectiveDTO, page, limit);
    }

    @GetMapping("admin/learningObjective/search")
    public ResponseEntity<?> searchLearningObjectiveADMIN(@RequestBody LearningObjectiveDTO learningObjectiveDTO,
                                                          @RequestParam String sortById,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int limit){
        return learningObjectiveService.searchSortFilterADMIN(learningObjectiveDTO, sortById, page, limit);
    }

    @PostMapping("admin/learningObjective/create")
    public ResponseEntity<?> createLearningObjective(@Valid @RequestBody LearningObjectiveDTO learningObjective) {
        return learningObjectiveService.save(learningObjective);
    }

    @PutMapping("admin/learningObjective/update")
    public ResponseEntity<?> updateLearningObjective(@RequestBody LearningObjectiveDTO learningObjective) {
        return learningObjectiveService.save(learningObjective);
    }

    @DeleteMapping("admin/learningObjective/delete/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return learningObjectiveService.changeStatus(id);
    }
}
