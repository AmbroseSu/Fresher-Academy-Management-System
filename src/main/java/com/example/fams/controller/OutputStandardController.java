package com.example.fams.controller;

import com.example.fams.dto.OutputStandardDTO;
import com.example.fams.services.IOutputStandardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class OutputStandardController {

    @Autowired
    @Qualifier("OutputStandardService")
    private IOutputStandardService outputStandardService;

    @PreAuthorize("hasAuthority('syllabus:Full_Access') || hasAuthority('syllabus:View') ||" +
            "hasAuthority('content:Full_Access') || hasAuthority('content:View')")
    @GetMapping("/outputStandard/{id}")
    public ResponseEntity<?> getOutputStandardById(@PathVariable Long id) {
        return outputStandardService.findById(id);
    }

    @PreAuthorize("hasAuthority('syllabus:Full_Access') || hasAuthority('syllabus:Create') ||" +
            "hasAuthority('content:Full_Access') || hasAuthority('content:Create')")
    @PostMapping("/outputStandard")
    public ResponseEntity<?> createOutputStandard(@Valid @RequestBody OutputStandardDTO outputStandardDTO) {
        return outputStandardService.save(outputStandardDTO);
    }

}
