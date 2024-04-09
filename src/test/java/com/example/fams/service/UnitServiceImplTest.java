package com.example.fams.service;

import com.example.fams.config.CustomValidationException;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.dto.UnitDTO;
import com.example.fams.entities.Content;
import com.example.fams.entities.Syllabus;
import com.example.fams.entities.Unit;
import com.example.fams.repository.ContentRepository;
import com.example.fams.repository.SyllabusRepository;
import com.example.fams.repository.UnitRepository;
import com.example.fams.services.ServiceUtils;
import com.example.fams.services.impl.UnitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.event.annotation.PrepareTestInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UnitServiceImplTest {
    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private SyllabusRepository syllabusRepository;

    @Mock
    private GenericConverter genericConverter;

    @Mock
    private ServiceUtils serviceUtils;

    @InjectMocks
    private UnitServiceImpl unitService;

    private UnitDTO unitDTO;
    private Unit unit;


    @Test
    void findAllByStatusTrue() {
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        List<Unit> entities = new ArrayList<>();
        entities.add(new Unit());
        entities.add(new Unit());

        // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
        when(unitRepository.findByStatusIsTrue(pageable)).thenReturn(entities);
        when(unitRepository.countAllByStatusIsTrue()).thenReturn(2L);
        when(genericConverter.toDTO(any(Unit.class), eq(UnitDTO.class)))
                .thenReturn(new UnitDTO());


        // * Test hàm trong service
        ResponseEntity<?> response = unitService.findAllByStatusTrue(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseDTO != null;
        assertEquals(2, ((List<?>) responseDTO.getContent()).size());
    }


    @Test
    void findAllByStatusTrue_EmptyResult() {
        // Arrange
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        List<Unit> entities = new ArrayList<>();

        // Mock unit repository to return an empty list
        when(unitRepository.findByStatusIsTrue(pageable)).thenReturn(entities);
        when(unitRepository.countAllByStatusIsTrue()).thenReturn(0L);

        // Act
        ResponseEntity<?> response = unitService.findAllByStatusTrue(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseDTO != null;
        assertEquals(0, ((List<?>) responseDTO.getContent()).size());
    }



    @Test
    void findById_oldEntity() {
        // Arrange
        Long id = 1L;
        Unit unit = new Unit();
        unit.setId(id);
        unit.setStatus(true);

        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setId(id);

        // Mock unit repository để trả về unit đã được tạo ở trên khi gọi phương thức findById()
        when(unitRepository.findById(id)).thenReturn(unit);
        // Mock genericConverter để trả về unitDTO đã được tạo ở trên khi gọi phương thức toDTO()
        when(genericConverter.toDTO(unit, UnitDTO.class)).thenReturn(unitDTO);

        // Act
        ResponseEntity<?> response = unitService.findById(id);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        UnitDTO result = (UnitDTO) responseDTO.getContent();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertEquals(id, result.getId());
    }
    @Test
    void findById_NotoldEntity() {
        // Arrange
        Long id = 1L;


        // Mock unit repository để trả về unit đã được tạo ở trên khi gọi phương thức findById()
        when(unitRepository.findById(id)).thenReturn(null);

        ResponseEntity<?> response = unitService.findById(id);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();


        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Unit not found", responseDTO.getDetails().get(0));

    }


    @Test
    void findAll() {
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        List<Unit> entities = new ArrayList<>();
        entities.add(new Unit());
        entities.add(new Unit());


        // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
        when(unitRepository.findAllBy(pageable)).thenReturn(entities);
        when(unitRepository.countAllBy()).thenReturn(2L);
        when(genericConverter.toDTO(any(Unit.class), eq(UnitDTO.class)))
                .thenReturn(new UnitDTO());


        // * Test hàm trong service
        ResponseEntity<?> response = unitService.findAll(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseDTO != null;
        assertEquals(2, ((List<?>) responseDTO.getContent()).size());
    }

    @Test
    void testCreateNewUnit_returnSuccess() {
        // Arrange
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setName("Test Unit");
        unitDTO.setStatus(true);

        Unit savedUnit = new Unit(); // Tạo một đối tượng Unit đã được lưu thành công
        savedUnit.setId(1L);
        savedUnit.setName("Test Unit");
        savedUnit.setStatus(true);

        // Mock behavior của unitRepository.save(any())
        when(unitRepository.save(any())).thenReturn(savedUnit);

        // Mock behavior của genericConverter.toDTO(any(Unit.class), eq(UnitDTO.class))
        when(genericConverter.toDTO(any(Unit.class), eq(UnitDTO.class))).thenReturn(unitDTO);
        when(genericConverter.toEntity(any(UnitDTO.class), eq(Unit.class))).thenReturn(savedUnit);

        // Act
        ResponseEntity<?> responseEntity = unitService.save(unitDTO);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
        // Add more assertions as needed
        assertEquals(unitDTO.getName(), ((UnitDTO) responseDTO.getContent()).getName());
//        assertEquals(unitDTO.getDuration(), ((UnitDTO) responseDTO.getContent()).getDuration());
        assertEquals(unitDTO.getStatus(), ((UnitDTO) responseDTO.getContent()).getStatus());
    }


    @Test
    void testCreateNewUnit_ReturnSyllabusIdsNotExist() {
        // Arrange
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setSyllabusId(1L); // Assuming syllabusId 1 doesn't exist

        // Mocking the behavior of syllabusRepository.existsById(1L) to return false
        Mockito.lenient().when(syllabusRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        CustomValidationException exception = assertThrows(CustomValidationException.class,
                () -> unitService.save(unitDTO));
        assertEquals("Syllabus with id [1] does not exist", exception.getMessage());
    }


    @Test
    void testUpdateUnit_returnSuccess() {
        // Arrange
        Unit unit = new Unit();
        unit.setId(1L);

        // Mocking necessary methods
        Mockito.lenient().when(unitRepository.findById((Long) any())).thenReturn(unit);
        when(unitRepository.save(any())).thenReturn(new Unit());
        when(genericConverter.toDTO(any(Unit.class), eq(UnitDTO.class)))
                .thenReturn(new UnitDTO());
        when(genericConverter.toEntity(any(UnitDTO.class), eq(Unit.class))).thenReturn(new Unit());

        // Act
        ResponseEntity<?> responseEntity = unitService.save(new UnitDTO());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
    }



    @Test
    void testChangeStatus_UnitNotFound() {
        // Given
        Long id = 1L;
        when(unitRepository.findById(id)).thenReturn(null);

        // When
        ResponseEntity<?> responseEntity = unitService.changeStatus(id);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        // Then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Unit not found", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
        assertEquals("Unit with id 1 not found", (responseDTO.getMessage()));
//        verify(unitRepository, never()).save(any()); // Ensure save is not called
    }

    @Test
    public void testChangeStatus_StatusChangedSuccessfully() {
        // Given
        Long id = 1L;
        Unit entity = new Unit();
        entity.setId(id);
        entity.setStatus(true); // Initial status is true
        when(unitRepository.findById(id)).thenReturn(entity);

        // When
        ResponseEntity<?> responseEntity = unitService.changeStatus(id);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Status changed successfully", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
        assertEquals(false, entity.getStatus()); // Ensure status is toggled
        verify(unitRepository, times(1)).save(entity); // Verify that save is called once
    }
    @Test
    public void testChangeStatus_StatusChangedSuccessfullyWithStatusIsFalse() {
        // Given
        Long id = 1L;
        Unit entity = new Unit();
        entity.setId(id);
        entity.setStatus(false); // Initial status is true
        when(unitRepository.findById(id)).thenReturn(entity);

        // When
        ResponseEntity<?> responseEntity = unitService.changeStatus(id);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Status changed successfully", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
        assertEquals(true, entity.getStatus()); // Ensure status is toggled
        verify(unitRepository, times(1)).save(entity); // Verify that save is called once
    }

    @Test
    void testCreateNewUnit_ReturnContentIdsNotExist() {
        // Arrange
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setContentIds(List.of(1L, 2L)); // Assuming contentIds 1 and 2 don't exist

        // Mocking the behavior of contentRepository.existsById(...) to return false for all ids
        when(contentRepository.existsById(String.valueOf(anyLong()))).thenReturn(false);

        // Act & Assert
        CustomValidationException exception = assertThrows(CustomValidationException.class,
                () -> unitService.save(unitDTO));
        assertEquals("Content with id 1 does not exist, Content with id 2 does not exist", exception.getMessage());
    }

    @Test
    void testCreateNewUnit_ContentIdsExist() {
        // Arrange
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setContentIds(List.of(1L, 2L)); // Assuming contentIds 1 and 2 exist

        // Mocking the behavior of contentRepository.existsById(...) to return true for all ids
        when(contentRepository.existsById(String.valueOf(anyLong()))).thenReturn(true);

        // Mocking necessary methods
        when(unitRepository.save(any())).thenReturn(new Unit());
        when(genericConverter.toDTO(any(Unit.class), eq(UnitDTO.class)))
                .thenReturn(new UnitDTO());
        when(genericConverter.toEntity(any(UnitDTO.class), eq(Unit.class))).thenReturn(new Unit());

        // Mocking the behavior of contentRepository.findById(...) to return dummy content objects
        when(contentRepository.findById(1L)).thenReturn(new Content());
        when(contentRepository.findById(2L)).thenReturn(new Content());

        // Act
        ResponseEntity<?> responseEntity = unitService.save(unitDTO);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
    }


//    @Test
//    void testCreateNewUnit_NullName() {
//        // Arrange
//        UnitDTO unitDTO = new UnitDTO();
//        unitDTO.setName(null); // Assuming name is null
//
//        // Act & Assert
//        CustomValidationException exception = assertThrows(CustomValidationException.class,
//                () -> unitService.save(unitDTO));
//        assertEquals("Unit Name must not be blank", exception.getMessage());
//    }
//
//
//    @Test
//    void testCreateNewUnit_EmptyName() {
//        // Arrange
//        UnitDTO unitDTO = new UnitDTO();
//        unitDTO.setName(""); // Assuming name is empty
//
//        // Act & Assert
//        CustomValidationException exception = assertThrows(CustomValidationException.class,
//                () -> unitService.save(unitDTO));
//        assertEquals("Unit Name must not be blank", exception.getMessage());
//    }

    @Test
    void testCreateNewUnit_DuplicateContentIds() {
        // Arrange
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setContentIds(List.of(1L, 1L)); // Assuming duplicate contentIds are provided

        // Act & Assert
        CustomValidationException exception = assertThrows(CustomValidationException.class,
                () -> unitService.save(unitDTO));
        assertEquals("Content with id 1 does not exist, Content with id 1 does not exist", exception.getMessage());
    }


    @Test
    void testCreateNewUnit_InvalidSyllabusId() {
        // Arrange
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setSyllabusId(-1L); // Assuming negative syllabusId is provided

        // Act & Assert
        CustomValidationException exception = assertThrows(CustomValidationException.class,
                () -> unitService.save(unitDTO));
        assertEquals("Syllabus with id [-1] does not exist", exception.getMessage());
    }




    @Test
    void testSearchSortFilterADMIN() {
        // Arrange
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setName("Test Unit");
        unitDTO.setDayNumber("123");
        unitDTO.setDuration(10);
        String sortById = "id"; // Assuming sorting by id
        int page = 1;
        int limit = 10;

        Pageable pageable = PageRequest.of(page - 1, limit);

        // Mock unit repository to return some units
        List<Unit> units = new ArrayList<>();
        units.add(new Unit());
        units.add(new Unit());
        when(genericConverter.toDTO(any(Unit.class), eq(UnitDTO.class))).thenReturn(unitDTO);
//        when(genericConverter.toEntity(any(UnitDTO.class), eq(Unit.class))).thenReturn(savedUnit);
        when(unitRepository.searchSortFilterADMIN(
                unitDTO.getName(),
                unitDTO.getDayNumber(),
                unitDTO.getDuration(),
                sortById,
                pageable
        )).thenReturn(units);

        // Mock count
        Long count = 2L;
        when(unitRepository.countSearchSortFilter(
                unitDTO.getName(),
                unitDTO.getDayNumber(),
                unitDTO.getDuration()
        )).thenReturn(count);

        // Act
        ResponseEntity<?> responseEntity = unitService.searchSortFilterADMIN(unitDTO, sortById, page, limit);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
//        assertEquals(page, responseDTO.getPage());
//        assertEquals(limit, responseDTO.getLimit());
//        assertEquals(count, responseDTO.getTotal());
        List<UnitDTO> result = (List<UnitDTO>) responseDTO.getContent();
        assertEquals(units.size(), result.size());
        // Add more assertions as needed
    }

    @Test
    void testSearchSortFilterADMIN_EmptyResult() {
        // Arrange
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setName("Test Unit");
        unitDTO.setDayNumber("123");
        unitDTO.setDuration(10);
        String sortById = "id"; // Assuming sorting by id
        int page = 1;
        int limit = 10;

        Pageable pageable = PageRequest.of(page - 1, limit);

        // Mock unit repository to return an empty list
        List<Unit> units = new ArrayList<>();
        when(unitRepository.searchSortFilterADMIN(
                unitDTO.getName(),
                unitDTO.getDayNumber(),
                unitDTO.getDuration(),
                sortById,
                pageable
        )).thenReturn(units);

        // Mock count
        Long count = 0L;
        when(unitRepository.countSearchSortFilter(
                unitDTO.getName(),
                unitDTO.getDayNumber(),
                unitDTO.getDuration()
        )).thenReturn(count);

        // Act
        ResponseEntity<?> responseEntity = unitService.searchSortFilterADMIN(unitDTO, sortById, page, limit);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        List<UnitDTO> result = (List<UnitDTO>) responseDTO.getContent();
        assertEquals(0, result.size());
        // Add more assertions as needed
    }

    @Test
    void testSearchSortFilter() {
        // Arrange
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setName("Test Unit");
        unitDTO.setDayNumber("123");
        unitDTO.setDuration(10);
        int page = 1;
        int limit = 10;

        Pageable pageable = PageRequest.of(page - 1, limit);

        // Mock unit repository to return some units
        List<Unit> units = new ArrayList<>();
        units.add(new Unit());
        units.add(new Unit());
        when(genericConverter.toDTO(any(Unit.class), eq(UnitDTO.class))).thenReturn(unitDTO);
        when(unitRepository.searchSortFilter(
                unitDTO.getName(),
                unitDTO.getDayNumber(),
                unitDTO.getDuration(),
                pageable
        )).thenReturn(units);

        // Mock count
        Long count = 2L;
        when(unitRepository.countSearchSortFilter(
                unitDTO.getName(),
                unitDTO.getDayNumber(),
                unitDTO.getDuration()
        )).thenReturn(count);

        // Act
        ResponseEntity<?> responseEntity = unitService.searchSortFilter(unitDTO, page, limit);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        List<UnitDTO> result = (List<UnitDTO>) responseDTO.getContent();
        assertEquals(units.size(), result.size());
        // Add more assertions as needed
    }

    @Test
    void testSearchSortFilter_EmptyResult() {
        // Arrange
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setName("Test Unit");
        unitDTO.setDayNumber("123");
        unitDTO.setDuration(10);
        int page = 1;
        int limit = 10;

        Pageable pageable = PageRequest.of(page - 1, limit);

        // Mock unit repository to return an empty list
        List<Unit> units = new ArrayList<>();
        when(unitRepository.searchSortFilter(
                unitDTO.getName(),
                unitDTO.getDayNumber(),
                unitDTO.getDuration(),
                pageable
        )).thenReturn(units);

        // Mock count
        Long count = 0L;
        when(unitRepository.countSearchSortFilter(
                unitDTO.getName(),
                unitDTO.getDayNumber(),
                unitDTO.getDuration()
        )).thenReturn(count);

        // Act
        ResponseEntity<?> responseEntity = unitService.searchSortFilter(unitDTO, page, limit);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        List<UnitDTO> result = (List<UnitDTO>) responseDTO.getContent();
        assertEquals(0, result.size());
        // Add more assertions as needed
    }


    @Test
    void testCheckExist_UnitExists() {
        // Arrange
        Long id = 1L;
        Unit unit = new Unit();
        unit.setId(id);
        when(unitRepository.findById(id)).thenReturn(unit);

        // Act
        Boolean result = unitService.checkExist(id);

        // Assert
        assertTrue(result);
    }

    @Test
    void testCheckExist_UnitNotExists() {
        // Arrange
        Long id = 1L;
        when(unitRepository.findById(id)).thenReturn(null);

        // Act
        Boolean result = unitService.checkExist(id);

        // Assert
        assertFalse(result);
    }











}
