package com.example.fams.services.impl;

import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;

import com.example.fams.dto.MaterialDTO;

import com.example.fams.entities.*;
import com.example.fams.repository.*;
import com.example.fams.services.IMaterialService;
import com.example.fams.services.ServiceUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("MaterialService")
public class MaterialServiceImpl implements IMaterialService {


    private final MaterialRepository materialRepository;
    private final SyllabusMaterialRepository syllabusMaterialRepository;
    private final SyllabusRepository syllabusRepository;
    private final GenericConverter genericConverter;

    public MaterialServiceImpl(MaterialRepository materialRepository,SyllabusMaterialRepository syllabusMaterialRepository,SyllabusRepository syllabusRepository, GenericConverter genericConverter,
                               LearningObjectiveRepository learningObjectiveRepository) {
        this.materialRepository = materialRepository;
        this.genericConverter = genericConverter;
        this.syllabusMaterialRepository = syllabusMaterialRepository;
        this.syllabusRepository = syllabusRepository;
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Material entity = materialRepository.findByStatusIsTrueAndId(id);
        if (entity!= null) {
            MaterialDTO result = convertMaterialToMaterialDTO(entity);
            return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
        }else {
            return ResponseUtil.error("Material not found", "Cannot Find Material", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        Pageable pageable = PageRequest.of(page-1, limit);
        List<Material> entities = materialRepository.findAllByStatusIsTrue(pageable);
        List<MaterialDTO> result = new ArrayList<>();
        convertListMaterialToMaterialDTO(entities, result);
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
        convertListMaterialToMaterialDTO(entities, result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                materialRepository.count());
    }

    @Override
    public ResponseEntity<?> save(MaterialDTO materialDTO) {
        ServiceUtils.errors.clear();
        List<Long> requestSyllabusIds = materialDTO.getSyllabusIds();
        Material entity;

        // * Validate requestDTO ( if left null, then can be updated later )
        if (requestSyllabusIds != null){
            ServiceUtils.validateSyllabusIds(requestSyllabusIds, syllabusRepository);
        }
        if (!ServiceUtils.errors.isEmpty()) {
            throw new CustomValidationException(ServiceUtils.errors);
        }

        // * For update request
        if (materialDTO.getId() != null){
            Material oldEntity = materialRepository.findById(materialDTO.getId());
            Material tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
            entity = convertDtoToEntity(materialDTO);
            entity = ServiceUtils.fillMissingAttribute(entity, tempOldEntity);
            syllabusMaterialRepository.deleteAllByMaterialId(materialDTO.getId());
            loadSyllabusMaterialFromListSyllabusId(requestSyllabusIds, entity.getId());
            entity.markModified();
            materialRepository.save(entity);
        }

        // * For create request
        else {
            materialDTO.setStatus(true);
            entity = convertDtoToEntity(materialDTO);
            materialRepository.save(entity);
            loadSyllabusMaterialFromListSyllabusId(requestSyllabusIds, entity.getId());
        }


        MaterialDTO result = convertMaterialToMaterialDTO(entity);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
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
    private void loadSyllabusMaterialFromListSyllabusId(List<Long> requestSyllabusIds, Long materialId) {
        if (requestSyllabusIds != null && !requestSyllabusIds.isEmpty()) {
            for (Long syllabusId : requestSyllabusIds) {
                Material material=materialRepository.findById(materialId);
                Syllabus syllabus=syllabusRepository.findOneById(syllabusId);
                if (material!= null && syllabus!= null) {
                    SyllabusMaterial syllabusMaterial = new SyllabusMaterial();
                    syllabusMaterial.setMaterial(material);
                    syllabusMaterial.setSyllabus(syllabus);
                    syllabusMaterialRepository.save(syllabusMaterial);
                }
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
        convertListMaterialToMaterialDTO(entities,result);
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
        convertListMaterialToMaterialDTO(entities, result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",

                page,
                limit,
                count);
    }
    private void convertListMaterialToMaterialDTO(List<Material> entities,List<MaterialDTO> result){
        for (Material entity : entities){
            MaterialDTO newMaterialDTO = convertMaterialToMaterialDTO(entity);
            result.add(newMaterialDTO);
        }
    }

    private MaterialDTO convertMaterialToMaterialDTO(Material entity){
        MaterialDTO newMaterialDTO = (MaterialDTO) genericConverter.toDTO(entity, MaterialDTO.class);
        List<Syllabus> syllabuses= syllabusMaterialRepository.findSyllabusesByMaterialId(entity.getId());
        if (syllabuses==null){
            newMaterialDTO.setSyllabusIds(null);
        }
        else {
            List<Long> SyllabusIds = syllabuses.stream().map(Syllabus::getId).toList();
            newMaterialDTO.setSyllabusIds(SyllabusIds);}
        return newMaterialDTO;
    }

    public Material convertDtoToEntity(MaterialDTO contentDTO) {
        Material material = new Material();
        material.setId(contentDTO.getId());
        material.setName(contentDTO.getName());
        material.setDescription(contentDTO.getDescription());
        material.setStatus(contentDTO.getStatus());
        return material;
    }
}
