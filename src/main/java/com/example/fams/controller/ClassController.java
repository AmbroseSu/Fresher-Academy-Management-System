package com.example.fams.controller;

import com.example.fams.dto.ClassDTO;
import com.example.fams.services.IClassService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ClassController {
    @Autowired
    @Qualifier("ClassService")
    private IClassService classService;

    @GetMapping("user/class")
    public ResponseEntity<?> getAllClassByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit) {
        return classService.findAllByStatusTrue(page, limit);
    }

    @GetMapping("admin/class")
    public ResponseEntity<?> getAllClasses(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int limit) {
        return classService.findAll(page, limit);
    }

    @GetMapping("user/class/{id}")
    public ResponseEntity<?> getByClassId(@PathVariable Long id) {
        return classService.findById(id);
    }

    @GetMapping("user/class/search")
    public ResponseEntity<?> searchClass(@RequestBody ClassDTO classDTO,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int limit) {
        return classService.searchSortFilter(classDTO, page, limit);
    }

    @GetMapping("admin/class/search")
    public ResponseEntity<?> searchClassADMIN(@RequestBody ClassDTO classDTO,
                                              @RequestParam(required = false) String sortById,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int limit) {
        return classService.searchSortFilterADMIN(classDTO, sortById, page, limit);
    }

    @PostMapping("admin/class")
    public ResponseEntity<?> createClass(@Valid @RequestBody ClassDTO classDTO) {
        return classService.save(classDTO);
    }

    @PutMapping("admin/class")
    public ResponseEntity<?> updateClass(@Valid @RequestBody ClassDTO classDTO) {
        return classService.save(classDTO);
    }

    @DeleteMapping("admin/class/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return classService.changeStatus(id);
    }
}
