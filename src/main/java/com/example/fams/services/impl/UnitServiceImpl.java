package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ContentDTO;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.SyllabusDTO;
import com.example.fams.dto.UnitDTO;
import com.example.fams.entities.Content;
import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.Syllabus;
import com.example.fams.entities.Unit;
import com.example.fams.repository.ContentRepository;
import com.example.fams.repository.UnitRepository;
import com.example.fams.services.IGenericService;
import com.example.fams.services.IUnitService;
import com.example.fams.services.ServiceUtils;
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
public class UnitServiceImpl implements IUnitService {

    private final UnitRepository unitRepository;
    private final ContentRepository contentRepository;
    private final GenericConverter genericConverter;

    public UnitServiceImpl(UnitRepository unitRepository, ContentRepository contentRepository, GenericConverter genericConverter) {
        this.unitRepository = unitRepository;
        this.contentRepository = contentRepository;
        this.genericConverter = genericConverter;
    }


    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Unit> units = unitRepository.findByStatusIsTrue(pageable);
        List<UnitDTO> result = new ArrayList<>();
        // ? Với mỗi unit, chuyển nó thành unitDTO (chưa có syllabusDTO và List<ContentDTO> ở trong)
        for (Unit unit : units) {
            UnitDTO newUnitDTO = (UnitDTO) genericConverter.toDTO(unit, UnitDTO.class);

            // * Lấy list Content từ unitId và lấy Syllabus từ syllabusId trong unitDTO
            List<Content> contents = unitRepository.findContentsByUnitId(unit.getId());
//            Syllabus syllabus = unitRepository.findSyllabusBySyllabusId(unit.getSyllabus().getId());

            // * Với mỗi content trong list content vừa lấy được, convert sang contentDTO rồi nhét vào list contentDTOS
            List<ContentDTO> contentDTOS = new ArrayList<>();
            for (Content content : contents) {
                ContentDTO newContentDTO = (ContentDTO) genericConverter.toDTO(content, ContentDTO.class);
                contentDTOS.add(newContentDTO);
            }
//            SyllabusDTO syllabusDTO = (SyllabusDTO) genericConverter.toDTO(syllabus, SyllabusDTO.class);

            // ! Set list contentDTO và syllabusDTO sau khi convert ở trên vào unitDTO
            newUnitDTO.setContentDTOs(contentDTOS);
//            newUnitDTO.setSyllabusDTO(syllabusDTO);

            // todo trả về List DTO đã có contentDTOs ở trong
            result.add(newUnitDTO);
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
        Unit unit;
        List<ContentDTO> requestContentDTOs = unitDTO.getContentDTOs();
        validateContentIds(unitDTO.getContentDTOs());
        // * For update request
        if (unitDTO.getId() != null){
            Unit oldEntity = unitRepository.findById(unitDTO.getId());
            Unit tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
            unit = (Unit) genericConverter.updateEntity(unitDTO, oldEntity);
            ServiceUtils.fillMissingAttribute(unit, tempOldEntity);
            unitRepository.deleteAllContentInUnitByUnitId(unitDTO.getId());
            loadContentsFromListContentIds(requestContentDTOs, unit.getId());
            unit.markModified();
            unitRepository.save(unit);
        }

        // * For create request
        else {
            unitDTO.setStatus(true);
            unit = (Unit) genericConverter.toEntity(unitDTO, Unit.class);
            unitRepository.save(unit);
            loadContentsFromListContentIds(requestContentDTOs, unit.getId());
        }


        UnitDTO result = (UnitDTO) genericConverter.toDTO(unit, UnitDTO.class);

        List<Content> contents = unitRepository.findContentsByUnitId(unit.getId());
        List<ContentDTO> contentDTOs = new ArrayList<>();
        for (Content content : contents) {
            ContentDTO newContentDTO = (ContentDTO) genericConverter.toDTO(content, ContentDTO.class);
            contentDTOs.add(newContentDTO);
        }
        result.setContentDTOs(contentDTOs);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");

    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Unit entity = unitRepository.findById(id);
        UnitDTO result = (UnitDTO) genericConverter.toDTO(entity, UnitDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Unit> units = unitRepository.findAll(pageable);
        List<UnitDTO> result = new ArrayList<>();
        // ? Với mỗi unit, chuyển nó thành unitDTO (chưa có syllabusDTO và List<ContentDTO> ở trong)
        for (Unit unit : units) {
            UnitDTO newUnitDTO = (UnitDTO) genericConverter.toDTO(unit, UnitDTO.class);

            // * Lấy list Content từ unitId và lấy Syllabus từ syllabusId trong unitDTO
            List<Content> contents = unitRepository.findContentsByUnitId(unit.getId());
//            Syllabus syllabus = unitRepository.findSyllabusBySyllabusId(unit.getSyllabus().getId());

            // * Với mỗi content trong list content vừa lấy được, convert sang contentDTO rồi nhét vào list contentDTOS
            List<ContentDTO> contentDTOS = new ArrayList<>();
            for (Content content : contents) {
                ContentDTO newContentDTO = (ContentDTO) genericConverter.toDTO(content, ContentDTO.class);
                contentDTOS.add(newContentDTO);
            }
//            SyllabusDTO syllabusDTO = (SyllabusDTO) genericConverter.toDTO(syllabus, SyllabusDTO.class);

            // ! Set list contentDTO và syllabusDTO sau khi convert ở trên vào unitDTO
            newUnitDTO.setContentDTOs(contentDTOS);
//            newUnitDTO.setSyllabusDTO(syllabusDTO);

            // todo trả về List DTO đã có contentDTOs ở trong
            result.add(newUnitDTO);
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
        Unit entity = unitRepository.findById(id);
        if (entity != null) {
            if (entity.getStatus()) {
                entity.setStatus(false);
            } else {
                entity.setStatus(true);
            }
            unitRepository.save(entity);
            return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
        } else {
            return ResponseUtil.error("Unit not found", "Unit with id " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    private void loadContentsFromListContentIds(List<ContentDTO> requestContentDTOs, Long unitId) {
        if (requestContentDTOs != null && !requestContentDTOs.isEmpty()) {
            for (ContentDTO contentDTO : requestContentDTOs) {
                Content content = contentRepository.findById(contentDTO.getId());
                content.setUnit(unitRepository.findById(unitId));
                contentRepository.save(content);
            }
        }
    }
    private void validateContentIds(List<ContentDTO> contentDTOs) {
        List<String> errors = new ArrayList<>();
        for (ContentDTO contentDTO : contentDTOs) {
            if (contentRepository.findById(contentDTO.getId()) == null) {
                errors.add("Content with id " + contentDTO.getId() + " does not exist");
            }
        }
        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join(", ", errors));
        }
    }
    public ResponseEntity<?> searchSortFilter(UnitDTO unitDTO, int page, int limit) {
        String name = unitDTO.getName();
        Integer duration = unitDTO.getDuration();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Unit> units = unitRepository.searchSortFilter(name, duration, pageable);
        List<UnitDTO> result = new ArrayList<>();
        Long count = unitRepository.countSearchSortFilter(name, duration);
        for (Unit unit : units){
            UnitDTO newUnitDTO = (UnitDTO) genericConverter.toDTO(unit, UnitDTO.class);
            result.add(newUnitDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                count);
    }

    @Override
    public ResponseEntity<?> searchSortFilterADMIN(UnitDTO unitDTO, String sortById, int page, int limit) {
        String name = unitDTO.getName();
        Integer duration = unitDTO.getDuration();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Unit> units = unitRepository.searchSortFilterADMIN(name, duration, sortById, pageable);
        List<UnitDTO> result = new ArrayList<>();
        Long count = unitRepository.countSearchSortFilter(name, duration);
        for (Unit unit : units){
            UnitDTO newUnitDTO = (UnitDTO) genericConverter.toDTO(unit, UnitDTO.class);
            result.add(newUnitDTO);
        }
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                count);
    }
}
