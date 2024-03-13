package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.MaterialDTO;
import com.example.fams.services.IMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('material:Full_Access') || hasAuthority('material:View')")
    @GetMapping("/material")
    public ResponseEntity<?> getAllMaterialByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int limit){
        return materialService.findAllByStatusTrue(page, limit);
    }

    @PreAuthorize("hasAuthority('material:Full_Access')")
    @GetMapping("/material/hidden")
    public ResponseEntity<?> getAllMaterial(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int limit){

        return  materialService.findAll(page, limit);
    }
    @PreAuthorize("hasAuthority('material:Full_Access') || hasAuthority('material:View')")
    @GetMapping("material/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id){

        return  materialService.findById(id);
    }

    @PreAuthorize("hasAuthority('material:Full_Access') || hasAuthority('material:Create')")
    @PostMapping("material")
    public ResponseEntity<?> createMaterial(@Valid @RequestBody MaterialDTO materialDTO){
        return materialService.save(materialDTO);
    }

    @PreAuthorize("hasAuthority('material:Full_Access') || hasAuthority('material:Modify')")
    @PutMapping("material/{id}")
    public ResponseEntity<?> updateMaterial(@Valid @RequestBody MaterialDTO material,@PathVariable(name = "id") Long id){
        if(materialService.checkExist(id)){
            material.setId(id);
            return materialService.save(material);

        }
        return ResponseUtil.error("Not found","Material not exist", HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('material:Full_Access')")
    @DeleteMapping("material/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return materialService.changeStatus(id);
    }

    @PreAuthorize("hasAuthority('material:Full_Access') || hasAuthority('material:View')")
    @GetMapping("material/search")
    public ResponseEntity<?> searchMaterial(@RequestBody MaterialDTO materialDTO,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit){
        return materialService.searchSortFilter(materialDTO, page, limit);
    }

    @PreAuthorize("hasAuthority('material:Full_Access')")
    @GetMapping("material/search/hidden")
    public ResponseEntity<?> searchMaterialADMIN(@RequestBody MaterialDTO materialDTO,
                                                          @RequestParam(required = false) String sortById,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int limit){
        return materialService.searchSortFilterADMIN(materialDTO, sortById, page, limit);
    }







}
