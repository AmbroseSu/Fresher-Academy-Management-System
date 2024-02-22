package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.MaterialDTO;
import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.Material;
import com.example.fams.repository.MaterialRepository;
import com.example.fams.services.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service("MaterialService")
public class MaterialServiceImpl implements IGenericService<MaterialDTO> {


    private final MaterialRepository materialRepository;
    private final GenericConverter genericConverter;
    public MaterialServiceImpl(MaterialRepository materialRepository, GenericConverter genericConverter) {
        this.materialRepository = materialRepository;
        this.genericConverter = genericConverter;
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Material entity = materialRepository.findByStatusIsTrueAndId(id);
        MaterialDTO result = (MaterialDTO) genericConverter.toDTO(entity,MaterialDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        Pageable pageable = PageRequest.of(page,limit);
        List<Material> entities = materialRepository.findAllByStatusIsTrue(pageable);
        List<MaterialDTO> result =  new ArrayList<>();

        for (Material entity: entities) {
            MaterialDTO dto = (MaterialDTO) genericConverter.toDTO(entity, MaterialDTO.class);
            result.add(dto);
        }

        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                materialRepository.countAllByStatusIsTrue());
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page-1,limit);
        List<Material> entities = materialRepository.findAllByOrderByIdDesc(pageable);
        List<MaterialDTO> result =  new ArrayList<>();

        for (Material entity: entities) {
            MaterialDTO dto = (MaterialDTO) genericConverter.toDTO(entity, MaterialDTO.class);
            result.add(dto);
        }

        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                materialRepository.count());
    }

    @Override
    public ResponseEntity<?> save(MaterialDTO material) {

        Material entity = new Material();
        if (material.getId() != null){
            Material oldEntity = materialRepository.findById(material.getId());
            Material tempOldEntity = cloneMaterial(oldEntity);


            entity = (Material) genericConverter.updateEntity(material, oldEntity);

        entity = fillMissingAttribute(entity,tempOldEntity);




        } else {
            material.setStatus(true);
            entity = (Material) genericConverter.toEntity(material, Material.class);
        }

        materialRepository.save(entity);
        MaterialDTO result = (MaterialDTO) genericConverter.toDTO(entity, MaterialDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");

    }

    private Material fillMissingAttribute(Material entity,Material tempOldEntity){
        List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = entity.getClass();
try {


// Traverse class hierarchy to collect fields from all superclasses
        while (currentClass != null) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            allFields.addAll(Arrays.asList(declaredFields));
            currentClass = currentClass.getSuperclass();
        }

        // Iterate over all fields
        for (Field field : allFields) {
            field.setAccessible(true); // Enable access to private fields if any

            try {
                Object newValue = field.get(entity); // Get the value of the field for the newEntity
                if (newValue == null ) {
                    // If the value is null, get the corresponding value from oldEntity
                    Object oldValue = field.get(tempOldEntity);
                    field.set(entity, oldValue); // Set the value of the field for the newEntity
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    return entity;
}catch (Exception e){
    throw  e;
}

    }
    private Material cloneMaterial(Material material) {
        Material clone = new Material();
        try {
            BeanUtils.copyProperties(clone, material);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
        return clone;
    }
    @Override
    public ResponseEntity<?> changeStatus(Long id) {
        Material entity = materialRepository.findById(id);
        if (entity != null) {
            if (entity.getStatus()) {
                entity.setStatus(false);
            } else {
                entity.setStatus(true);
            }
            materialRepository.save(entity);
            return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
        } else {
            return ResponseUtil.error("Material not found", "Cannot change status of non-existing Material", HttpStatus.NOT_FOUND);
        }
    }
}
