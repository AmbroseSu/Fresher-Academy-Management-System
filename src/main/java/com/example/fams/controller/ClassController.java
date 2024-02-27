package com.example.fams.controller;

import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.services.IClassService;
import com.example.fams.services.IGenericService;
import com.example.fams.services.ILearningObjectiveService;
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

  @GetMapping("user/class/findAllByStatusTrue")
  public ResponseEntity<?> getAllClassByStatusTrue(@RequestParam int page, @RequestParam int limit) {
    return classService.findAllByStatusTrue(page, limit);
  }

  @GetMapping("admin/class/findAll")
  public ResponseEntity<?> getAllClasses(@RequestParam int page, @RequestParam int limit) {
    return classService.findAll(page, limit);
  }

  @GetMapping("user/class/search")
  public ResponseEntity<?> searchLearningObjective(@RequestBody ClassDTO classDTO,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return classService.searchSortFilter(classDTO, page, limit);
  }

  @GetMapping("admin/class/search")
  public ResponseEntity<?> searchClassADMIN(@RequestBody ClassDTO classDTO,
      @RequestParam String sortById,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return classService.searchSortFilterADMIN(classDTO, sortById, page, limit);
  }

  @PostMapping("admin/class/create")
  public ResponseEntity<?> createLearningObjective(@RequestBody ClassDTO classDTO) {
    return classService.save(classDTO);
  }

  @PutMapping("admin/class/update")
  public ResponseEntity<?> updateClass(@RequestBody ClassDTO classDTO) {
    return classService.save(classDTO);
  }

  @DeleteMapping("admin/class/delete/{id}")
  public ResponseEntity<?> changeStatus(@PathVariable Long id) {
    return classService.changeStatus(id);
  }
}
