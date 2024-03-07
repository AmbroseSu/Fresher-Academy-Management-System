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

    @GetMapping("user/learningObjective")
    public ResponseEntity<?> getAllLearningObjectivesByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "10") int limit) {
        return learningObjectiveService.findAllByStatusTrue(page, limit);
    }

    @GetMapping("admin/learningObjective")
    public ResponseEntity<?> getAllLearningObjectives(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int limit) {
        return learningObjectiveService.findAll(page, limit);
    }

    @GetMapping("user/learningObjective/{id}")
    public ResponseEntity<?> getByLearningObjectiveId(@PathVariable Long id) {
        return learningObjectiveService.findById(id);
    }

    @GetMapping("user/learningObjective/search")
    public ResponseEntity<?> searchLearningObjective(@RequestBody LearningObjectiveDTO learningObjectiveDTO,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit){
        return learningObjectiveService.searchSortFilter(learningObjectiveDTO, page, limit);
    }

    @GetMapping("admin/learningObjective/search")
    public ResponseEntity<?> searchLearningObjectiveADMIN(@RequestBody LearningObjectiveDTO learningObjectiveDTO,
                                                          @RequestParam(required = false) String sortById,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int limit){
        return learningObjectiveService.searchSortFilterADMIN(learningObjectiveDTO, sortById, page, limit);
    }

    @PostMapping("admin/learningObjective")
    public ResponseEntity<?> createLearningObjective(@Valid @RequestBody LearningObjectiveDTO learningObjective) {
        return learningObjectiveService.save(learningObjective);
    }

    @PutMapping("admin/learningObjective")
    public ResponseEntity<?> updateLearningObjective(@Valid @RequestBody LearningObjectiveDTO learningObjective) {
        return learningObjectiveService.save(learningObjective);
    }

    @DeleteMapping("admin/learningObjective/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return learningObjectiveService.changeStatus(id);
    }
}
