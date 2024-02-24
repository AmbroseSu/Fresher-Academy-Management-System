package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.UnitDTO;
import com.example.fams.entities.Unit;
import com.example.fams.repository.UnitRepository;
import com.example.fams.services.IGenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("UnitService")
public class UnitServiceImpl implements IGenericService<UnitDTO> {

    private final UnitRepository unitRepository;
    private final GenericConverter genericConverter;

    @Autowired
    public UnitServiceImpl(UnitRepository unitRepository, GenericConverter genericConverter) {
        this.unitRepository = unitRepository;
        this.genericConverter = genericConverter;
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Unit> entities = unitRepository.findByStatusIsTrue(pageable);
        List<UnitDTO> result = new ArrayList<>();
        for (Unit entity : entities) {
            UnitDTO newDTO = (UnitDTO) genericConverter.toDTO(entity, UnitDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                unitRepository.count());
    }

    @Override
    public ResponseEntity<?> save(UnitDTO unitDTO) {
        Unit entity = new Unit();
        if (unitDTO.getId() != null) {
            Unit oldEntity = unitRepository.findById(unitDTO.getId()).orElse(null);
            if (oldEntity != null) {
                entity = (Unit) genericConverter.updateEntity(unitDTO, oldEntity);
            } else {
                // Handle entity not found error
                return ResponseUtil.error("Unit not found", "Cannot update non-existing Unit", HttpStatus.NOT_FOUND);
            }
        } else {
            entity = (Unit) genericConverter.toEntity(unitDTO, Unit.class);
        }

        unitRepository.save(entity);
        UnitDTO result = (UnitDTO) genericConverter.toDTO(entity, UnitDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Unit entity = unitRepository.findById(id).orElse(null);
        if (entity != null) {
            UnitDTO result = (UnitDTO) genericConverter.toDTO(entity, UnitDTO.class);
            return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
        } else {
            return ResponseUtil.error("Unit not found", "Unit with id " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Unit> entities = unitRepository.findAll(pageable);
        List<UnitDTO> result = new ArrayList<>();
        for (Unit entity : entities) {
            UnitDTO newDTO = (UnitDTO) genericConverter.toDTO(entity, UnitDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                unitRepository.count());
    }

    @Override
    public ResponseEntity<?> changeStatus(Long id) {
        Unit entity = unitRepository.findById(id).orElse(null);
        if (entity != null) {
            entity.setStatus(!entity.getStatus());
            unitRepository.save(entity);
            return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
        } else {
            return ResponseUtil.error("Unit not found", "Unit with id " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }
}
