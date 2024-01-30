package com.example.fams.controller;

import com.example.fams.entities.LearningObjective;
import com.example.fams.services.LearningObjectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learningObjective")
public class LearningObjectiveController {
    @Autowired
    private LearningObjectiveService learningObjectiveService;

    @GetMapping
    public List<LearningObjective> getAllLearningObjectives() {
        return learningObjectiveService.getAllLearningObjectives();
    }

    @GetMapping("/{code}")
    public LearningObjective getLearningObjectiveByCode(@PathVariable String code) {
        return learningObjectiveService.getLearningObjectiveByCode(code);
    }

    @PostMapping
    public LearningObjective createLearningObjective(@RequestBody LearningObjective learningObjective) {
        return learningObjectiveService.createLearningObjective(learningObjective);
    }

    @PutMapping("/{code}")
    public LearningObjective updateLearningObjective(@PathVariable String code, @RequestBody LearningObjective updatedLearningObjective) {
        return learningObjectiveService.updateLearningObjective(code, updatedLearningObjective);
    }

    @DeleteMapping("/{code}")
    public void deleteLearningObjective(@PathVariable String code) {
        learningObjectiveService.deleteLearningObjective(code);
    }
}
