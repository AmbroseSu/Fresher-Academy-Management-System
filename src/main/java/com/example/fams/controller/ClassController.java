package com.example.fams.controller;

import com.example.fams.dto.ClassDTO;
import com.example.fams.services.IGenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ClassController {
  @Autowired
  @Qualifier("ClassService")
  private IGenericService<ClassDTO> classService;

  @GetMapping("user/class/findAllByStatusTrue")
  public ResponseEntity<?> getAllClassByStatusTrue(@RequestParam int page, @RequestParam int limit) {
    return classService.findAllByStatusTrue(page, limit);
  }

  @GetMapping("admin/class/findAll")
  public ResponseEntity<?> getAllLearningObjectives(@RequestParam int page, @RequestParam int limit) {
    return classService.findAll(page, limit);
  }

  @PostMapping("admin/class/create")
  public ResponseEntity<?> createLearningObjective(@RequestBody ClassDTO classDTO) {
    return classService.save(classDTO);
  }

  @PutMapping("admin/class/update")
  public ResponseEntity<?> updateLearningObjective(@RequestBody ClassDTO classDTO) {
    return classService.save(classDTO);
  }

  @DeleteMapping("admin/class/delete/{id}")
  public ResponseEntity<?> changeStatus(@PathVariable Long id) {
    return classService.changeStatus(id);
  }
}
