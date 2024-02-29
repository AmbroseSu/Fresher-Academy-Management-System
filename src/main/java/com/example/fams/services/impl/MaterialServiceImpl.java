package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.MaterialDTO;
import com.example.fams.dto.SyllabusDTO;
import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.Material;
import com.example.fams.entities.Syllabus;
import com.example.fams.entities.SyllabusMaterial;
import com.example.fams.repository.LearningObjectiveRepository;
import com.example.fams.repository.MaterialRepository;
import com.example.fams.repository.SyllabusMaterialRepository;
import com.example.fams.repository.SyllabusRepository;
import com.example.fams.services.IGenericService;
import com.example.fams.services.IMaterialService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("MaterialService")
public class MaterialServiceImpl implements IMaterialService {


    private final MaterialRepository materialRepository;
    private final SyllabusMaterialRepository syllabusMaterialRepository;
    private final SyllabusRepository syllabusRepository;
    private final GenericConverter genericConverter;
    private final LearningObjectiveRepository learningObjectiveRepository;

    public MaterialServiceImpl(MaterialRepository materialRepository,SyllabusMaterialRepository syllabusMaterialRepository,SyllabusRepository syllabusRepository, GenericConverter genericConverter,
                               LearningObjectiveRepository learningObjectiveRepository) {
        this.materialRepository = materialRepository;
        this.genericConverter = genericConverter;
        this.syllabusMaterialRepository = syllabusMaterialRepository;
        this.syllabusRepository = syllabusRepository;
        this.learningObjectiveRepository = learningObjectiveRepository;
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Material entity = materialRepository.findByStatusIsTrueAndId(id);
        MaterialDTO result = (MaterialDTO) genericConverter.toDTO(entity, MaterialDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        Pageable pageable = PageRequest.of(page-1, limit);
        List<Material> entities = materialRepository.findAllByStatusIsTrue(pageable);
        List<MaterialDTO> result = new ArrayList<>();

        for (Material entity : entities) {
            MaterialDTO newMaterialDTO = (MaterialDTO) genericConverter.toDTO(entity, MaterialDTO.class);


            List<Syllabus> syllabuses = syllabusMaterialRepository.findSyllabusesByMaterialId(entity.getId());

            List<SyllabusDTO> syllabusDTOS = new ArrayList<>();
            for (Syllabus syllabus : syllabuses) {
                SyllabusDTO newSyllabusDTO = (SyllabusDTO) genericConverter.toDTO(syllabus, SyllabusDTO.class);
                syllabusDTOS.add(newSyllabusDTO);
            }

            newMaterialDTO.setSyllabusDTOs(syllabusDTOS);

            result.add(newMaterialDTO);
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
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Material> entities = materialRepository.findAllByOrderByIdDesc(pageable);
        List<MaterialDTO> result = new ArrayList<>();

        for (Material entity : entities) {
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
    public ResponseEntity<?> save(MaterialDTO materialDTO) {
        List<SyllabusDTO> requestSyllabusDTOs = materialDTO.getSyllabusDTOs();

        Material entity = new Material();
        if (materialDTO.getId() != null) {
            Material oldEntity = materialRepository.findById(materialDTO.getId());
            Material tempOldEntity = cloneMaterial(oldEntity);
            entity = (Material) genericConverter.updateEntity(materialDTO, oldEntity);
            entity = fillMissingAttribute(entity, tempOldEntity);
            syllabusMaterialRepository.deleteAllByMaterialId(materialDTO.getId());
            loadSyllabusMaterialFromListSyllabusId(requestSyllabusDTOs,entity.getId());
            entity.markModified();
            materialRepository.save(entity);
        } else {
            materialDTO.setStatus(true);
            entity = (Material) genericConverter.toEntity(materialDTO, Material.class);
            materialRepository.save(entity);
            loadSyllabusMaterialFromListSyllabusId(requestSyllabusDTOs,entity.getId());
        }
        MaterialDTO result = (MaterialDTO) genericConverter.toDTO(entity, MaterialDTO.class);

        List<Syllabus> syllabuses = syllabusMaterialRepository.findSyllabusesByMaterialId(entity.getId());
        List<SyllabusDTO> syllabusDTOS = new ArrayList<>();
        for (Syllabus syllabus : syllabuses) {
            SyllabusDTO newSyllabusDTO = (SyllabusDTO) genericConverter.toDTO(syllabus, SyllabusDTO.class);
            syllabusDTOS.add(newSyllabusDTO);
        }
        result.setSyllabusDTOs(syllabusDTOS);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");

    }

    private Material fillMissingAttribute(Material entity, Material tempOldEntity) {
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
                    if (newValue == null) {
                        // If the value is null, get the corresponding value from oldEntity
                        Object oldValue = field.get(tempOldEntity);
                        field.set(entity, oldValue); // Set the value of the field for the newEntity
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return entity;
        } catch (Exception e) {
            throw e;
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
            entity.setStatus(!entity.getStatus());
            materialRepository.save(entity);
            return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
        } else {
            return ResponseUtil.error("Material not found", "Cannot change status of non-existing Material", HttpStatus.NOT_FOUND);
        }
    }
    private void loadSyllabusMaterialFromListSyllabusId(List<SyllabusDTO> requestSyllabusDTOs, Long materialId) {
        if (requestSyllabusDTOs != null && !requestSyllabusDTOs.isEmpty()) {
            for (SyllabusDTO syllabusDTO : requestSyllabusDTOs) {
                SyllabusMaterial syllabusMaterial = new SyllabusMaterial();
                syllabusMaterial.setMaterial(materialRepository.findById(materialId));
                syllabusMaterial.setSyllabus(syllabusRepository.findById(syllabusDTO.getId()));
                syllabusMaterialRepository.save(syllabusMaterial);
            }
        }
    }
    @Override
    public ResponseEntity<?> searchSortFilter(MaterialDTO materialDTO, int page, int limit) {
        String name = materialDTO.getName();
        String description = materialDTO.getDescription();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Material> entities = materialRepository.searchSortFilter(name,description, pageable);
        List<MaterialDTO> result = new ArrayList<>();
        Long count = materialRepository.countSearchSortFilter(name,description);
        for (Material entity : entities){
            MaterialDTO newDTO = (MaterialDTO) genericConverter.toDTO(entity, MaterialDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                count);
    }
    @Override
    public ResponseEntity<?> searchSortFilterADMIN(MaterialDTO materialDTO, String sortById, int page, int limit) {
        String name = materialDTO.getName();
        String description = materialDTO.getDescription();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Material> entities = materialRepository.searchSortFilterADMIN(name, description, sortById, pageable);
        List<MaterialDTO> result = new ArrayList<>();
        Long count = materialRepository.countSearchSortFilter(name,description);
        for (Material entity : entities){
            MaterialDTO newDTO = (MaterialDTO) genericConverter.toDTO(entity, MaterialDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                count);
    }
}
