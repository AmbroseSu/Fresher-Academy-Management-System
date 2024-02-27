package com.example.fams.controller;

import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.ContentDTO;
import com.example.fams.services.IClassService;
import com.example.fams.services.IContentService;
import com.example.fams.services.IGenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ContentController {
  @Autowired
  @Qualifier("ContentService")
  private IContentService contentService;

  @GetMapping("user/content/findAllByStatusTrue")
  public ResponseEntity<?> getAllContentByStatusTrue(@RequestParam int page, @RequestParam int limit) {
    return contentService.findAllByStatusTrue(page, limit);
  }

  @GetMapping("admin/content/findAll")
  public ResponseEntity<?> getAllContent(@RequestParam int page, @RequestParam int limit) {
    return contentService.findAll(page, limit);
  }

  @GetMapping("user/content/findById/{id}")
  public ResponseEntity<?> getByClassId(@PathVariable Long id) {
    return contentService.findById(id);
  }

  @GetMapping("user/content/search")
  public ResponseEntity<?> searchContent(@RequestBody ContentDTO contentDTO,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return contentService.searchSortFilter(contentDTO, page, limit);
  }

  @GetMapping("admin/content/search")
  public ResponseEntity<?> searchContentADMIN(@RequestBody ContentDTO contentDTO,
      @RequestParam String sortById,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return contentService.searchSortFilterADMIN(contentDTO, sortById, page, limit);
  }

  @PostMapping("admin/content/create")
  public ResponseEntity<?> createContent(@RequestBody ContentDTO contentDTO) {
    return contentService.save(contentDTO);
  }

  @PutMapping("admin/content/update")
  public ResponseEntity<?> updateContent(@RequestBody ContentDTO contentDTO) {
    return contentService.save(contentDTO);
  }

  @DeleteMapping("admin/content/delete/{id}")
  public ResponseEntity<?> changeStatus(@PathVariable Long id) {
    return contentService.changeStatus(id);
  }
}
