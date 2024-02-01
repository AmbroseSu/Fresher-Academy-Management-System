package com.example.fams.controller;

import com.example.fams.entities.Class;

import com.example.fams.services.ClassService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/class")
public class ClassController {

  private final ClassService classService;

  public ClassController(ClassService classService) {
    this.classService = classService;
  }

  @PostMapping("/saveclass")
  //@PreAuthorize("hasRole('USER')")
  public ResponseEntity<String> saveClass(@RequestBody Class classes) {
    try {
      classService.save(classes);
      return ResponseEntity.ok("Successfully");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/viewclass")
  public ResponseEntity<List<Class>> viewClass(){
    try {
      List<Class> classes = classService.findAll();
      return ResponseEntity.ok(classes);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.notFound().build();
    }
  }


  @PostMapping("/deleteclass")
  public ResponseEntity<String> deleteClass(@RequestParam String code){
    try {
      classService.delete(code);
      return ResponseEntity.ok("Successfully");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.notFound().build();
    }
  }


}
