package com.example.fams.controller;

import com.example.fams.entities.Content;
import com.example.fams.services.ContentService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {

  private final ContentService contentService;

  public ContentController(ContentService contentService) {
    this.contentService = contentService;
  }

  @PostMapping("/savecontent")
  public ResponseEntity<String> saveClass(@RequestBody Content content) {
    try {
      contentService.save(content);
      return ResponseEntity.ok("Successfully");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/viewcontent")
  public ResponseEntity<List<Content>> viewClass(){
    try {
      List<Content> content = contentService.findAll();
      return ResponseEntity.ok(content);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.notFound().build();
    }
  }


}
