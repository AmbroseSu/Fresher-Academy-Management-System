package com.example.fams.controller;

import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.ContentDTO;
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
public class ContentController {
  @Autowired
  @Qualifier("ContentService")
  private IGenericService<ContentDTO> contentService;

  @GetMapping("user/content/findAllByStatusTrue")
  public ResponseEntity<?> getAllClassByStatusTrue(@RequestParam int page, @RequestParam int limit) {
    return contentService.findAllByStatusTrue(page, limit);
  }

  @GetMapping("admin/content/findAll")
  public ResponseEntity<?> getAllLearningObjectives(@RequestParam int page, @RequestParam int limit) {
    return contentService.findAll(page, limit);
  }

  @PostMapping("admin/content/create")
  public ResponseEntity<?> createLearningObjective(@RequestBody ContentDTO contentDTO) {
    return contentService.save(contentDTO);
  }

  @PutMapping("admin/content/update")
  public ResponseEntity<?> updateLearningObjective(@RequestBody ContentDTO contentDTO) {
    return contentService.save(contentDTO);
  }

  @DeleteMapping("admin/content/delete/{id}")
  public ResponseEntity<?> changeStatus(@PathVariable Long id) {
    return contentService.changeStatus(id);
  }
}
