package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.entities.LearningObjective;
import com.example.fams.services.IGenericService;
import com.example.fams.services.ILearningObjectiveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class LearningObjectiveController {
    @Autowired
    @Qualifier("LearningObjectiveService")
    private ILearningObjectiveService learningObjectiveService;
    @PreAuthorize("hasAuthority('learningObjective:Full_Access') || hasAuthority('learningObjective:View')")
    @GetMapping("/learningObjective")
    public ResponseEntity<?> getAllLearningObjectivesByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "10") int limit) {
        return learningObjectiveService.findAllByStatusTrue(page, limit);
    }
    @PreAuthorize("hasAuthority('learningObjective:Full_Access') || hasAuthority('learningObjective:View')")
    @GetMapping("/learningObjective/hidden")
    public ResponseEntity<?> getAllLearningObjectives(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int limit) {
        return learningObjectiveService.findAll(page, limit);
    }
    @PreAuthorize("hasAuthority('learningObjective:Full_Access') || hasAuthority('learningObjective:View')")
    @GetMapping("/learningObjective/{id}")
    public ResponseEntity<?> getByLearningObjectiveId(@PathVariable Long id) {
        return learningObjectiveService.findById(id);
    }
    @PreAuthorize("hasAuthority('learningObjective:Full_Access') || hasAuthority('learningObjective:View')")
    @GetMapping("/learningObjective/search")
    public ResponseEntity<?> searchLearningObjective(@RequestBody LearningObjectiveDTO learningObjectiveDTO,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit){
        return learningObjectiveService.searchSortFilter(learningObjectiveDTO, page, limit);
    }
    @PreAuthorize("hasAuthority('learningObjective:Full_Access') || hasAuthority('learningObjective:View')")
    @GetMapping("/learningObjective/search/admin")
    public ResponseEntity<?> searchLearningObjectiveADMIN(@RequestBody LearningObjectiveDTO learningObjectiveDTO,
                                                          @RequestParam(required = false) String sortById,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int limit){
        return learningObjectiveService.searchSortFilterADMIN(learningObjectiveDTO, sortById, page, limit);
    }
    @PreAuthorize("hasAuthority('learningObjective:Full_Access') || hasAuthority('learningObjective:Create')")
    @PostMapping("/learningObjective")
    public ResponseEntity<?> createLearningObjective(@Valid @RequestBody LearningObjectiveDTO learningObjective) {
        return learningObjectiveService.save(learningObjective);
    }
    @PreAuthorize("hasAuthority('learningObjective:Full_Access') || hasAuthority('learningObjective:Modify')")
    @PutMapping("/learningObjective/{id}")
    public ResponseEntity<?> updateLearningObjective(@Valid @RequestBody LearningObjectiveDTO learningObjective, @PathVariable(name ="id") Long id) {
        if(learningObjectiveService.checkExist(id)){
            learningObjective.setId(id);
            return learningObjectiveService.save(learningObjective);
        }
        return ResponseUtil.error("Not found","LearningObjective not exist", HttpStatus.NOT_FOUND);
    }
    @PreAuthorize("hasAuthority('learningObjective:Full_Access')")
    @DeleteMapping("/learningObjective/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return learningObjectiveService.changeStatus(id);
    }
}
