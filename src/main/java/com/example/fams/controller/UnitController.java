package com.example.fams.controller;

import com.example.fams.dto.UnitDTO;
import com.example.fams.services.IGenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UnitController {

    @Autowired
    @Qualifier("UnitService")
    private IGenericService<UnitDTO> unitService;

    @GetMapping("user/unit/findAllByStatusTrue")
    public ResponseEntity<?> getAllUnitsByStatusTrue(@RequestParam int page, @RequestParam int limit) {
        return unitService.findAllByStatusTrue(page, limit);
    }

    @GetMapping("admin/unit/findAll")
    public ResponseEntity<?> getAllUnits(@RequestParam int page, @RequestParam int limit) {
        return unitService.findAll(page, limit);
    }

    @PostMapping("admin/unit/create")
    public ResponseEntity<?> createUnit(@RequestBody UnitDTO unit) {
        return unitService.save(unit);
    }

    @PutMapping("admin/unit/update")
    public ResponseEntity<?> updateUnit(@RequestBody UnitDTO unit) {
        return unitService.save(unit);
    }

    @DeleteMapping("admin/unit/delete/{id}")
    public ResponseEntity<?> deleteUnit(@PathVariable Long id) {
        return unitService.changeStatus(id);
    }
}
