package com.example.fams.controller;

import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.SyllabusDTO;
import com.example.fams.services.IGenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SyllabusController {
    @Autowired
    @Qualifier("SyllabusService")
    private IGenericService<SyllabusDTO> syllabusService;

    @GetMapping("user/syllabus/findAllByStatusTrue")
    public ResponseEntity<?> getAllSyllabusByStatusTrue(@RequestParam int page, @RequestParam int limit) {
        return syllabusService.findAllByStatusTrue(page, limit);
    }
    @GetMapping("admin/syllabus/findAll")
    public ResponseEntity<?> getAllSyllabus(@RequestParam int page, @RequestParam int limit) {
        return syllabusService.findAll(page, limit);
    }
    @PostMapping("admin/syllabus/create")
    public ResponseEntity<?> createSyllabus(@RequestBody SyllabusDTO syllabus) {
        return syllabusService.save(syllabus);
    }
    @PutMapping("admin/syllabus/update")
    public ResponseEntity<?> updateSyllabus(@RequestBody SyllabusDTO syllabus) {
        return syllabusService.save(syllabus);
    }
    @DeleteMapping("admin/syllabus/delete/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return syllabusService.changeStatus(id);
    }
}

