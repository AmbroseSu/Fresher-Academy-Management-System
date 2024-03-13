package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.ClassDTO;
import com.example.fams.services.IClassService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ClassController {
    @Autowired
    @Qualifier("ClassService")
    private IClassService classService;

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:View')")
    @GetMapping("/class")
    public ResponseEntity<?> getAllClassByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit) {
        return classService.findAllByStatusTrue(page, limit);
    }

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:View')")
    @GetMapping("/class/hidden")
    public ResponseEntity<?> getAllClasses(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int limit) {
        return classService.findAll(page, limit);
    }

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:View')")
    @GetMapping("/class/{id}")
    public ResponseEntity<?> getByClassId(@PathVariable Long id) {
        return classService.findById(id);
    }

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:View')")
    @GetMapping("/class/search")
    public ResponseEntity<?> searchClass(@RequestBody ClassDTO classDTO,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int limit) {
        return classService.searchSortFilter(classDTO, page, limit);
    }

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:View')")
    @GetMapping("/class/search/admin")
    public ResponseEntity<?> searchClassADMIN(@RequestBody ClassDTO classDTO,
                                              @RequestParam(required = false) String sortById,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int limit) {
        return classService.searchSortFilterADMIN(classDTO, sortById, page, limit);
    }
    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:Create')")
    @PostMapping("/class")
    public ResponseEntity<?> createClass(@Valid @RequestBody ClassDTO classDTO) {
        return classService.save(classDTO);
    }

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:Modify')")
    @PutMapping("/class/{id}")
    public ResponseEntity<?> updateClass(@Valid @RequestBody ClassDTO classDTO, @PathVariable(name = "id") Long id) {
        if(classService.checkExist(id)){
            classDTO.setId(id);
            return classService.save(classDTO);

        }
        return ResponseUtil.error("Not found","Unit not exist", HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('class:Full_Access')")
    @DeleteMapping("/class/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return classService.changeStatus(id);
    }
}
