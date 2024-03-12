package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.MaterialDTO;
import com.example.fams.services.IMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin
public class MaterialController {
    @Autowired
    @Qualifier("MaterialService")
    private IMaterialService materialService;

    @GetMapping("user/material")
    public ResponseEntity<?> getAllMaterialByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int limit){
        return materialService.findAllByStatusTrue(page, limit);
    }

    @GetMapping("admin/material")
    public ResponseEntity<?> getAllMaterial(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int limit){

        return  materialService.findAll(page, limit);
    }
    @GetMapping("admin/material/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id){

        return  materialService.findById(id);
    }

    @PostMapping("admin/material")
    public ResponseEntity<?> createMaterial(@Valid @RequestBody MaterialDTO materialDTO){
        return materialService.save(materialDTO);
    }

    @PutMapping("admin/material/{id}")
    public ResponseEntity<?> updateMaterial(@Valid @RequestBody MaterialDTO material,@PathVariable(name = "id") Long id){
        if(materialService.checkExist(id)){
            material.setId(id);
            return materialService.save(material);

        }
        return ResponseUtil.error("Not found","Material not exist", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("admin/material/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return materialService.changeStatus(id);
    }
    @GetMapping("user/material/search")
    public ResponseEntity<?> searchMaterial(@RequestBody MaterialDTO materialDTO,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit){
        return materialService.searchSortFilter(materialDTO, page, limit);
    }

    @GetMapping("admin/material/search")
    public ResponseEntity<?> searchMaterialADMIN(@RequestBody MaterialDTO materialDTO,
                                                          @RequestParam(required = false) String sortById,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int limit){
        return materialService.searchSortFilterADMIN(materialDTO, sortById, page, limit);
    }







}
