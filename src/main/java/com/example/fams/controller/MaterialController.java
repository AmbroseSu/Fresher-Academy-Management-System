package com.example.fams.controller;

import com.example.fams.dto.MaterialDTO;
import com.example.fams.services.IMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MaterialController {
    @Autowired
    @Qualifier("MaterialService")
    private IMaterialService materialService;

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
    @GetMapping("user/material/search")
    public ResponseEntity<?> searchMaterial(@RequestBody MaterialDTO materialDTO,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit){
        return materialService.searchSortFilter(materialDTO, page, limit);
    }

    @GetMapping("admin/material/search")
    public ResponseEntity<?> searchMaterialADMIN(@RequestBody MaterialDTO materialDTO,
                                                          @RequestParam String sortById,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int limit){
        return materialService.searchSortFilterADMIN(materialDTO, sortById, page, limit);
    }







}
