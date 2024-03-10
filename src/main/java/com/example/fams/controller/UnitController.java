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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UnitController {

    @Autowired
    @Qualifier("UnitService")
    private IUnitService unitService;

    @GetMapping("user/unit/{id}")
    public ResponseEntity<?> getByUnitId(@PathVariable Long id) {
        return unitService.findById(id);
    }


    @GetMapping("user/unit")
    public ResponseEntity<?> getAllUnitsByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit) {
        return unitService.findAllByStatusTrue(page, limit);
    }

    @GetMapping("admin/unit")
    public ResponseEntity<?> getAllUnits(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int limit) {
        return unitService.findAll(page, limit);
    }

    @PostMapping("admin/unit")
    public ResponseEntity<?> createUnit(@Valid @RequestBody UnitDTO unit) {
        return unitService.save(unit);
    }

    @PutMapping("admin/unit/{id}")
    public ResponseEntity<?> updateUnit(@Valid @RequestBody UnitDTO unit, @PathVariable(name = "id") Long id) {

        if(unitService.checkExist(id)){
            unit.setId(id);
            return unitService.save(unit);

        }
        return ResponseUtil.error("Not found","Unit not exist", HttpStatus.NOT_FOUND);    }

    @DeleteMapping("admin/unit/{id}")
    public ResponseEntity<?> deleteUnit(@PathVariable Long id) {
        return unitService.changeStatus(id);
    }

    @GetMapping("user/unit/search")
    public ResponseEntity<?> searchUnit(@RequestBody UnitDTO unitDTO,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit){
        return unitService.searchSortFilter(unitDTO, page, limit);
    }

    @GetMapping("admin/unit/search")
    public ResponseEntity<?> searchUnitADMIN(@RequestBody UnitDTO unitDTO,
                                             @RequestParam(required = false) String sortById,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int limit){
        return unitService.searchSortFilterADMIN(unitDTO, sortById, page, limit);
    }
}
