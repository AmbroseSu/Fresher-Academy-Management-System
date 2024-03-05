package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.MaterialDTO;
import com.example.fams.dto.SyllabusDTO;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.entities.*;
import com.example.fams.repository.*;
import com.example.fams.services.ISyllabusService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("SyllabusService")
public class SyllabusServiceImpl implements ISyllabusService {

    private final SyllabusRepository syllabusRepository;
    private final SyllabusObjectiveRepository syllabusObjectiveRepository;
    private final GenericConverter genericConverter;
    private final TrainingProgramRepository trainingProgramRepository;
    private final LearningObjectiveRepository learningObjectiveRepository;
    private final SyllabusTrainingProgramRepository syllabusTrainingProgramRepository;
    private final MaterialRepository materialRepository;
    private final SyllabusMaterialRepository syllabusMaterialRepository;

    public SyllabusServiceImpl(SyllabusRepository syllabusRepository, SyllabusObjectiveRepository syllabusObjectiveRepository, GenericConverter genericConverter, TrainingProgramRepository trainingProgramRepository, LearningObjectiveRepository learningObjectiveRepository, SyllabusTrainingProgramRepository syllabusTrainingProgramRepository, MaterialRepository materialRepository, SyllabusMaterialRepository syllabusMaterialRepository) {
        this.syllabusRepository = syllabusRepository;
        this.syllabusObjectiveRepository = syllabusObjectiveRepository;
        this.genericConverter = genericConverter;
        this.trainingProgramRepository = trainingProgramRepository;
        this.learningObjectiveRepository = learningObjectiveRepository;
        this.syllabusTrainingProgramRepository = syllabusTrainingProgramRepository;
        this.materialRepository = materialRepository;
        this.syllabusMaterialRepository = syllabusMaterialRepository;
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Syllabus entity = syllabusRepository.findByStatusIsTrueAndId(id);
        SyllabusDTO result = (SyllabusDTO) genericConverter.toDTO(entity, SyllabusDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Syllabus> entities = syllabusRepository.findAllByStatusIsTrue(pageable);
        List<SyllabusDTO> result = new ArrayList<>();
        for (Syllabus entity : entities) {
            SyllabusDTO newDTO = (SyllabusDTO) genericConverter.toDTO(entity, SyllabusDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                syllabusRepository.count());
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Syllabus> entities = syllabusRepository.findAll(pageable);
        List<SyllabusDTO> result = new ArrayList<>();
        for (Syllabus entity : entities) {
            SyllabusDTO newDTO = (SyllabusDTO) genericConverter.toDTO(entity, SyllabusDTO.class);
            result.add(newDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                syllabusRepository.count());
    }

    @Override
    public ResponseEntity<?> save(SyllabusDTO syllabusDTO) {
        List<LearningObjectiveDTO> requestLearningObjecttiveDTOs = syllabusDTO.getLearningObjectiveDTOs();
        List<TrainingProgramDTO> requestTrainingProgramDTOs = syllabusDTO.getTrainingProgramDTOs();
        List<MaterialDTO> requestMaterialDTOs = syllabusDTO.getMaterialDTOs();
        Syllabus entity;

        if(syllabusDTO.getId() != null){

            // Xử lí Update các giá trị cũ

            // Lấy Entity cũ ra
            Syllabus oldEntity = syllabusRepository.findById(syllabusDTO.getId()).get();
            // Clone cái cũ thành 1 thg entity khác
            Syllabus tempOldEntity = cloneSyllabus(oldEntity);
            // Update entity cũ bằng DTO nhập vào
            entity = (Syllabus) genericConverter.updateEntity(syllabusDTO, oldEntity);
            // Thêm nhg attribute còn thiếu từ entity cũ vào entity mới
            entity = fillMissingAttribute(entity, tempOldEntity);

            // Xử lí quan hệ

            // Xóa quan hệ cũ trong bảng phụ
            syllabusObjectiveRepository.deleteAllBySyllabusId(syllabusDTO.getId());
            // Update quan hệ mới từ DTO
            loadSyllabusObjectiveFromListSyllabusId(requestLearningObjecttiveDTOs, entity.getId());
            loadTrainingProgramFromListSyllabusId(requestTrainingProgramDTOs, entity.getId());
            loadMaterialFromListSyllabusId(requestMaterialDTOs, entity.getId());
            // Đánh dấu là đã fix
            entity.markModified();
            //save
            syllabusRepository.save(entity);
        } else {
            syllabusDTO.setStatus(true);
            entity = (Syllabus) genericConverter.toEntity(syllabusDTO, Syllabus.class);
            syllabusRepository.save(entity);
            loadSyllabusObjectiveFromListSyllabusId(requestLearningObjecttiveDTOs, entity.getId());
            loadTrainingProgramFromListSyllabusId(requestTrainingProgramDTOs, entity.getId());
            loadMaterialFromListSyllabusId(requestMaterialDTOs, entity.getId());
        }
        SyllabusDTO result = (SyllabusDTO) genericConverter.toDTO(entity, SyllabusDTO.class);
        List<LearningObjective> learningObjectives = syllabusObjectiveRepository.findLearningObjectiveBySyllabusId(entity.getId());
        List<LearningObjectiveDTO> learningObjectiveDTOs = new ArrayList<>();
        for(LearningObjective  learningObjective : learningObjectives){
            LearningObjectiveDTO newLearningObjectiveDTO = (LearningObjectiveDTO) genericConverter.toDTO(learningObjective, LearningObjectiveDTO.class);
            learningObjectiveDTOs.add(newLearningObjectiveDTO);
        }
        result.setLearningObjectiveDTOs(learningObjectiveDTOs);

        List<TrainingProgram> trainingPrograms = syllabusTrainingProgramRepository.findTrainingProgramBySyllabusId(entity.getId());
        List<TrainingProgramDTO> trainingProgramDTOs = new ArrayList<>();
        for(TrainingProgram trainingProgram: trainingPrograms){
            TrainingProgramDTO newTrainingProgramDTO = (TrainingProgramDTO) genericConverter.toDTO(trainingProgram, TrainingProgramDTO.class);
            trainingProgramDTOs.add(newTrainingProgramDTO);
        }
        result.setTrainingProgramDTOs(trainingProgramDTOs);


        List<Material> materials = syllabusMaterialRepository.findMaterialBySyllabusesId(entity.getId());
        List<MaterialDTO> MDTO = new ArrayList<>();
        for(Material material : materials){
            MaterialDTO newDTO = (MaterialDTO) genericConverter.toDTO(material, MaterialDTO.class);
            MDTO.add(newDTO);
        }
        result.setMaterialDTOs(MDTO);

        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }
    private void loadSyllabusObjectiveFromListSyllabusId(List<LearningObjectiveDTO> requestLearningObjectiveDTOs, Long syllabusId) {
        if (requestLearningObjectiveDTOs != null && !requestLearningObjectiveDTOs.isEmpty()) {
            for (LearningObjectiveDTO learningObjectiveDTO : requestLearningObjectiveDTOs) {
                SyllabusObjective syllabusObjective = new SyllabusObjective();
                syllabusObjective.setLearningObjective(learningObjectiveRepository.findById(learningObjectiveDTO.getId()));
                syllabusObjective.setSyllabus(syllabusRepository.findOneById(syllabusId));
                syllabusObjectiveRepository.save(syllabusObjective);
            }
        }
    }

    private void loadTrainingProgramFromListSyllabusId(List<TrainingProgramDTO> requestTrainingProgramDTOs, Long syllabusId) {
        if (requestTrainingProgramDTOs != null && !requestTrainingProgramDTOs.isEmpty()) {
            for (TrainingProgramDTO trainingProgramDTO : requestTrainingProgramDTOs) {
                SyllabusTrainingProgram syllabusTrainingProgram = new SyllabusTrainingProgram();
                syllabusTrainingProgram.setTrainingProgram(trainingProgramRepository.findById(trainingProgramDTO.getId()).get());
                syllabusTrainingProgram.setSyllabus(syllabusRepository.findOneById(syllabusId));
                syllabusTrainingProgramRepository.save(syllabusTrainingProgram);
            }
        }
    }

    private void loadMaterialFromListSyllabusId(List<MaterialDTO> requestMaterialDTOs, Long syllabusId) {
        if (requestMaterialDTOs != null && !requestMaterialDTOs.isEmpty()) {
            for (MaterialDTO materialDTO : requestMaterialDTOs) {
                SyllabusMaterial syllabusMaterial = new SyllabusMaterial();
                syllabusMaterial.setMaterial(materialRepository.findById(materialDTO.getId()));
                syllabusMaterial.setSyllabus(syllabusRepository.findOneById(syllabusId));
                syllabusMaterialRepository.save(syllabusMaterial);
            }
        }
    }


    private Syllabus cloneSyllabus(Syllabus syllabus){
        Syllabus clone = new Syllabus();
        try {
            BeanUtils.copyProperties(clone, syllabus);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
        return clone;
    }
    private Syllabus fillMissingAttribute(Syllabus entity, Syllabus tempOldEntity){
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

    @Override
    public ResponseEntity<?> changeStatus(Long id) {
        Syllabus entity = syllabusRepository.findOneById(id);
        if (entity != null) {
            if (entity.getStatus()) {
                entity.setStatus(false);
            } else {
                entity.setStatus(true);
            }
            syllabusRepository.save(entity);
            return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
        } else {
            return ResponseUtil.error("Syllabus not found", "Cannot change status of non-existing Syllabus", HttpStatus.NOT_FOUND);
        }
    }


    @Override
    public ResponseEntity<?> searchSortFilter(SyllabusDTO syllabusDTO, int page, int limit) {
        String name = syllabusDTO.getName();
        String code = syllabusDTO.getCode();
        Long timeAllocation = syllabusDTO.getTimeAllocation();
        String description = syllabusDTO.getDescription();
        Boolean isApproved = syllabusDTO.getIsApproved();
        Boolean isActive = syllabusDTO.getIsActive();
        String version = syllabusDTO.getVersion();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Syllabus> entities = syllabusRepository.searchSortFilter(name, code, timeAllocation, description, isApproved, isActive, version, pageable);
        List<SyllabusDTO> result = new ArrayList<>();
        Long count = syllabusRepository.countSearchSortFilter(name, code, timeAllocation, description, isApproved, isActive, version);
        for (Syllabus entity : entities){
            SyllabusDTO newDTO = (SyllabusDTO) genericConverter.toDTO(entity, SyllabusDTO.class);
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
    public ResponseEntity<?> searchSortFilterADMIN(SyllabusDTO syllabusDTO, String sortById, int page, int limit) {
        String name = syllabusDTO.getName();
        String code = syllabusDTO.getCode();
        Long timeAllocation = syllabusDTO.getTimeAllocation();
        String description = syllabusDTO.getDescription();
        Boolean isApproved = syllabusDTO.getIsApproved();
        Boolean isActive = syllabusDTO.getIsActive();
        String version = syllabusDTO.getVersion();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Syllabus> entities = syllabusRepository.searchSortFilterADMIN(name, code, timeAllocation, description, isApproved, isActive, version, sortById, pageable);
        List<SyllabusDTO> result = new ArrayList<>();
        Long count = syllabusRepository.countSearchSortFilter(name, code, timeAllocation, description, isApproved, isActive, version);
        for (Syllabus entity : entities){
            SyllabusDTO newDTO = (SyllabusDTO) genericConverter.toDTO(entity, SyllabusDTO.class);
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
