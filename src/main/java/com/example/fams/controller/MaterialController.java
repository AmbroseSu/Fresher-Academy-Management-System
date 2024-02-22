package com.example.fams.controller;

import com.example.fams.dto.MaterialDTO;
import com.example.fams.services.IGenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MaterialController {
    @Autowired
    @Qualifier("MaterialService")
    private IGenericService<MaterialDTO> materialService;

    @GetMapping("user/material/findAllByStatusTrue")
    public ResponseEntity<?> getAllMaterialByStatusTrue(@RequestParam int page , @RequestParam int limit){
        return materialService.findAllByStatusTrue(page, limit);
    }

    @GetMapping("admin/material/findAll")
    public ResponseEntity<?> getAllMaterial(@RequestParam int page, @RequestParam int limit){

        return  materialService.findAll(page, limit);
    }
    @GetMapping("admin/material/findById/{id}")
    public ResponseEntity<?> getAllMaterial(@PathVariable("id") Long id){

        return  materialService.findById(id);
    }

    @PostMapping("admin/material/create")
    public ResponseEntity<?> createMaterial(@RequestBody MaterialDTO materialDTO){
        return materialService.save(materialDTO);
    }

    @PutMapping("admin/material/update")
    public ResponseEntity<?> updateMaterial(@RequestBody MaterialDTO materialDTO){
        return materialService.save(materialDTO);
    }

    @DeleteMapping("admin/material/delete/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return materialService.changeStatus(id);
    }







}
