package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.UnitDTO;
import com.example.fams.services.IGenericService;
import com.example.fams.services.IUnitService;
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
public class UnitController {

    @Autowired
    @Qualifier("UnitService")
    private IUnitService unitService;

    @PreAuthorize("hasAuthority('unit:Full_Access') || hasAuthority('unit:View')")
    @GetMapping("/unit/{id}")
    public ResponseEntity<?> getByUnitId(@PathVariable Long id) {
        return unitService.findById(id);
    }


    @PreAuthorize("hasAuthority('unit:Full_Access') || hasAuthority('unit:View')")
    @GetMapping("/unit")
    public ResponseEntity<?> getAllUnitsByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit) {
        return unitService.findAllByStatusTrue(page, limit);
    }

    @PreAuthorize("hasAuthority('unit:Full_Access')")
    @GetMapping("/unit/hidden")
    public ResponseEntity<?> getAllUnits(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int limit) {
        return unitService.findAll(page, limit);
    }

    @PreAuthorize("hasAuthority('unit:Full_Access') || hasAuthority('unit:Create')")
    @PostMapping("/unit")
    public ResponseEntity<?> createUnit(@Valid @RequestBody UnitDTO unit) {
        return unitService.save(unit);
    }

    @PreAuthorize("hasAuthority('unit:Full_Access') || hasAuthority('unit:Modify')")
    @PutMapping("/unit/{id}")
    public ResponseEntity<?> updateUnit(@Valid @RequestBody UnitDTO unit, @PathVariable(name = "id") Long id) {

        if(unitService.checkExist(id)){
            unit.setId(id);
            return unitService.save(unit);

        }
        return ResponseUtil.error("Not found","Unit not exist", HttpStatus.NOT_FOUND);    }

    @PreAuthorize("hasAuthority('unit:Full_Access')")
    @DeleteMapping("/unit/{id}")
    public ResponseEntity<?> deleteUnit(@PathVariable Long id) {
        return unitService.changeStatus(id);
    }

    @PreAuthorize("hasAuthority('unit:Full_Access') || hasAuthority('unit:View')")
    @GetMapping("/unit/search")
    public ResponseEntity<?> searchUnit(@RequestBody UnitDTO unitDTO,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit){
        return unitService.searchSortFilter(unitDTO, page, limit);
    }

    @PreAuthorize("hasAuthority('unit:Full_Access')")
    @GetMapping("/unit/search/hidden")
    public ResponseEntity<?> searchUnitADMIN(@RequestBody UnitDTO unitDTO,
                                             @RequestParam(required = false) String sortById,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int limit){
        return unitService.searchSortFilterADMIN(unitDTO, sortById, page, limit);
    }
}
