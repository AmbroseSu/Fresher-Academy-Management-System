package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.config.CustomValidationException;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.UnitDTO;
import com.example.fams.entities.Content;
import com.example.fams.entities.Unit;
import com.example.fams.repository.ContentRepository;
import com.example.fams.repository.SyllabusRepository;
import com.example.fams.repository.UnitRepository;
import com.example.fams.services.IUnitService;
import com.example.fams.services.ServiceUtils;
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
    private final SyllabusRepository syllabusRepository;
    private final GenericConverter genericConverter;

    public UnitServiceImpl(UnitRepository unitRepository, ContentRepository contentRepository,
            SyllabusRepository syllabusRepository, GenericConverter genericConverter) {
        this.unitRepository = unitRepository;
        this.contentRepository = contentRepository;
        this.syllabusRepository = syllabusRepository;
        this.genericConverter = genericConverter;
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Unit> units = unitRepository.findByStatusIsTrue(pageable);
        List<UnitDTO> result = new ArrayList<>();
        // ? Với mỗi unit, chuyển nó thành unitDTO (chưa có syllabusDTO và
        // List<ContentDTO> ở trong)
        convertListUnitToListUnitDTO(units, result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                unitRepository.count());
    }

    @Override
    public ResponseEntity<?> save(UnitDTO unitDTO) {
        ServiceUtils.errors.clear();
        Unit unit;
        List<Long> requestContentIds = unitDTO.getContentIds();
        Long requestSyllabusId = unitDTO.getSyllabusId();

        // * Validate requestDTO ( if left null, then can be updated later )
        if (requestContentIds != null) {
            ServiceUtils.validateContentIds(requestContentIds, contentRepository);
        }
        if (requestSyllabusId != null) {
            ServiceUtils.validateSyllabusIds(List.of(requestSyllabusId), syllabusRepository);
        }
        if (!ServiceUtils.errors.isEmpty()) {
            throw new CustomValidationException(ServiceUtils.errors);
        }

        // * For update request
        if (unitDTO.getId() != null) {
            Unit oldEntity = unitRepository.findById(unitDTO.getId());
            Unit tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
            // unit = convertDtoToEntity(unitDTO, syllabusRepository, contentRepository);
            unit = (Unit) genericConverter.toEntity(unitDTO, Unit.class);
            ServiceUtils.fillMissingAttribute(unit, tempOldEntity);

            if (requestSyllabusId != null) {
                unit.setSyllabus(syllabusRepository.findOneById(requestSyllabusId));
            }
            if (requestContentIds != null) {
                contentRepository.findAllByUnitId(unitDTO.getId()).stream()
                        .peek(content -> content.setUnit(null))
                        .forEach(contentRepository::save);
            }
            loadContentsFromListContentIds(requestContentIds, unit.getId());

            unit.markModified();
            unitRepository.save(unit);
        }
        // * For create request
        else {
            unitDTO.setStatus(true);
            unit = (Unit) genericConverter.toEntity(unitDTO, Unit.class);
            if (requestSyllabusId != null) {
                unit.setSyllabus(syllabusRepository.findOneById(requestSyllabusId));
            }
            unitRepository.save(unit);
            loadContentsFromListContentIds(requestContentIds, unit.getId());
        }

        UnitDTO result = convertUnitToUnitDTO(unit);
        // result.setSyllabusId(requestSyllabusId);
        // result.setContentIds(requestContentIds);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }

    public Boolean checkExist(Long id) {
        Unit unit = unitRepository.findById(id);
        return unit != null;
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Unit entity = unitRepository.findById(id);
        if (entity != null) {
            UnitDTO result = convertUnitToUnitDTO(entity);
            return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
        } else {
            return ResponseUtil.error("Unit not found", "Cannot Find Unit", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Unit> units = unitRepository.findAllBy(pageable);
        List<UnitDTO> result = new ArrayList<>();
        // ? Với mỗi unit, chuyển nó thành unitDTO (chưa có syllabusId và List
        // contentIds ở trong)
        convertListUnitToListUnitDTO(units, result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                unitRepository.count());
    }

    public ResponseEntity<?> searchSortFilter(UnitDTO unitDTO, int page, int limit) {
        String name = unitDTO.getName();
        String dayNumber = unitDTO.getDayNumber();
        Integer duration = unitDTO.getDuration();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Unit> units = unitRepository.searchSortFilter(name, dayNumber, duration, pageable);
        List<UnitDTO> result = new ArrayList<>();
        Long count = unitRepository.countSearchSortFilter(name, dayNumber, duration);
        convertListUnitToListUnitDTO(units, result);
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
        String dayNumber = unitDTO.getDayNumber();
        Integer duration = unitDTO.getDuration();
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Unit> units = unitRepository.searchSortFilterADMIN(name, dayNumber, duration, sortById, pageable);
        List<UnitDTO> result = new ArrayList<>();
        Long count = unitRepository.countSearchSortFilter(name, dayNumber, duration);
        convertListUnitToListUnitDTO(units, result);
        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                count);
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

    private void loadContentsFromListContentIds(List<Long> requestContentIds, Long unitId) {
        if (requestContentIds != null && !requestContentIds.isEmpty()) {
            for (Long contentId : requestContentIds) {
                Content content = contentRepository.findById(contentId);
                content.setUnit(unitRepository.findById(unitId));
                contentRepository.save(content);
            }
        }
    }

    // public Unit convertDtoToEntity(UnitDTO unitDTO, SyllabusRepository
    // syllabusRepository, ContentRepository contentRepository) {
    // Unit unit = new Unit();
    // unit.setId(unitDTO.getId());
    // unit.setName(unitDTO.getName());
    //// unit.setDuration(unitDTO.getDuration());
    // unit.setStatus(unitDTO.getStatus());
    //
    // // Fetch the Syllabus using the provided syllabusId
    // Syllabus syllabus = syllabusRepository.findOneById(unitDTO.getSyllabusId());
    // unit.setSyllabus(syllabus);
    //
    // // Fetch the Content objects using the provided contentIds
    // List<Content> contents = new ArrayList<>();
    // if (unitDTO.getContentIds() != null){
    // for (Long id : unitDTO.getContentIds()) {
    // Content content = contentRepository.findById(id);
    // if (content != null){
    // content.setUnit(unit);
    // contents.add(content);
    // }
    // }
    // }
    // unit.setContents(contents);
    // return unit;
    // }

    private void convertListUnitToListUnitDTO(List<Unit> units, List<UnitDTO> result) {
        result.addAll(units.stream()
                .map(this::convertUnitToUnitDTO)
                .toList());
    }

    private UnitDTO convertUnitToUnitDTO(Unit unit) {
        UnitDTO newUnitDTO = (UnitDTO) genericConverter.toDTO(unit, UnitDTO.class);
        // * Lấy list Content từ unitId và lấy Syllabus từ syllabusId trong unitDTO
        List<Content> contents = unitRepository.findContentsByUnitId(unit.getId());

        // ! Set list contentIds và syllabusId sau khi convert ở trên vào unitDTO

        if (contents == null)
            newUnitDTO.setContentIds(null);
        else {
            Long duration = contents.stream()
                    .mapToLong(Content::getDuration)
                    .sum();
            List<Long> contentIds = contents.stream()
                    .map(Content::getId)
                    .toList();
            newUnitDTO.setContentIds(contentIds);
            newUnitDTO.setDuration(duration.intValue());
        }

        if (unit.getSyllabus() == null)
            newUnitDTO.setSyllabusId(null);
        else
            newUnitDTO.setSyllabusId(unit.getSyllabus().getId());
        return newUnitDTO;
    }
}
