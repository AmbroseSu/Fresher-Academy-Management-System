package com.example.fams.controller;

import com.example.fams.dto.SyllabusDTO;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.services.IGenericService;
import com.example.fams.services.ISyllabusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SyllabusController {

    @Autowired
    @Qualifier("SyllabusService")
    private ISyllabusService syllabusService;

    @GetMapping("user/syllabus/{id}")
    public ResponseEntity<?> getBySyllabusId(@PathVariable Long id) {
        return syllabusService.findById(id);
    }

    @GetMapping("user/syllabus")
    public ResponseEntity<?> getAllSyllabusByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int limit) {
        return syllabusService.findAllByStatusTrue(page, limit);
    }

    @GetMapping("admin/syllabus")
    public ResponseEntity<?> getAllSyllabus(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int limit) {
        return syllabusService.findAll(page, limit);
    }
    @GetMapping("user/syllabus/search")
    public ResponseEntity<?> searchSyllabus(@RequestBody SyllabusDTO syllabusDTO,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int limit){
        return syllabusService.searchSortFilter(syllabusDTO, page, limit);
    }
    @GetMapping("admin/syllabus/search")
    public ResponseEntity<?> searchSyllabusADMIN(@RequestBody SyllabusDTO syllabusDTO,
                                                        @RequestParam(required = false) String sortById,
                                                        @RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int limit){
        return syllabusService.searchSortFilterADMIN(syllabusDTO, sortById, page, limit);
    }

    @PostMapping("admin/syllabus")
    public ResponseEntity<?> createSyllabus(@Valid @RequestBody SyllabusDTO syllabusDTO) {
        return syllabusService.save(syllabusDTO);
    }

    @PutMapping("admin/syllabus")
    public ResponseEntity<?> updateSyllabus(@Valid @RequestBody SyllabusDTO syllabusDTO) {
        return syllabusService.save(syllabusDTO);
    }

    @DeleteMapping("admin/syllabus/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return syllabusService.changeStatus(id);
    }
}
