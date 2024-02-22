package com.example.fams.controller;

import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.entities.LearningObjective;
import com.example.fams.services.IGenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class LearningObjectiveController {
    @Autowired
    @Qualifier("LearningObjectiveService")
    private IGenericService<LearningObjectiveDTO> learningObjectiveService;

    @GetMapping("user/learningObjective/findAllByStatusTrue")
    public ResponseEntity<?> getAllLearningObjectivesByStatusTrue(@RequestParam int page, @RequestParam int limit) {
        return learningObjectiveService.findAllByStatusTrue(page, limit);
    }

    @GetMapping("admin/learningObjective/findAll")
    public ResponseEntity<?> getAllLearningObjectives(@RequestParam int page, @RequestParam int limit) {
        return learningObjectiveService.findAll(page, limit);
    }

    @PostMapping("admin/learningObjective/create")
    public ResponseEntity<?> createLearningObjective(@RequestBody LearningObjectiveDTO learningObjective) {
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
