package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.ContentDTO;
import com.example.fams.services.IClassService;
import com.example.fams.services.IContentService;
import com.example.fams.services.IGenericService;
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
public class ContentController {
  @Autowired
  @Qualifier("ContentService")
  private IContentService contentService;

  @PreAuthorize("hasAuthority('content:Full_Access') || hasAuthority('content:View')")
  @GetMapping("/content")
  public ResponseEntity<?> getAllContentByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit) {
    return contentService.findAllByStatusTrue(page, limit);
  }

  @PreAuthorize("hasAuthority('content:Full_Access') || hasAuthority('content:View')")
  @GetMapping("/content/hidden")
  public ResponseEntity<?> getAllContent(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int limit) {
    return contentService.findAll(page, limit);
  }

  @PreAuthorize("hasAuthority('content:Full_Access') || hasAuthority('content:View')")
  @GetMapping("/content/{id}")
  public ResponseEntity<?> getByClassId(@PathVariable Long id) {
    return contentService.findById(id);
  }

  @PreAuthorize("hasAuthority('content:Full_Access') || hasAuthority('content:View')")
  @GetMapping("/content/search")
  public ResponseEntity<?> searchContent(@RequestBody ContentDTO contentDTO,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return contentService.searchSortFilter(contentDTO, page, limit);
  }

  @PreAuthorize("hasAuthority('content:Full_Access') || hasAuthority('content:View')")
  @GetMapping("/content/search/admin")
  public ResponseEntity<?> searchContentADMIN(@RequestBody ContentDTO contentDTO,
      @RequestParam(required = false) String sortById,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return contentService.searchSortFilterADMIN(contentDTO, sortById, page, limit);
  }

  @PreAuthorize("hasAuthority('content:Full_Access') || hasAuthority('content:Create')")
  @PostMapping("/content")
  public ResponseEntity<?> createContent(@Valid @RequestBody ContentDTO contentDTO) {
    return contentService.save(contentDTO);
  }

  @PreAuthorize("hasAuthority('content:Full_Access') || hasAuthority('content:Modify')")
  @PutMapping("/content/{id}")
  public ResponseEntity<?> updateContent(@Valid @RequestBody ContentDTO contentDTO, @PathVariable(name = "id") Long id) {
    if(contentService.checkExist(id)){
      contentDTO.setId(id);
      return contentService.save(contentDTO);
    }
    return ResponseUtil.error("Not found","Unit not exist", HttpStatus.NOT_FOUND);
  }

  @PreAuthorize("hasAuthority('content:Full_Access')")
  @DeleteMapping("/content/{id}")
  public ResponseEntity<?> changeStatus(@PathVariable Long id) {
    return contentService.changeStatus(id);
  }
}
