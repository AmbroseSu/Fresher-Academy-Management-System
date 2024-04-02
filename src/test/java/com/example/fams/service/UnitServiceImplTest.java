package com.example.fams.service;

import com.example.fams.config.CustomValidationException;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.dto.UnitDTO;
import com.example.fams.entities.Unit;
import com.example.fams.repository.ContentRepository;
import com.example.fams.repository.SyllabusRepository;
import com.example.fams.repository.UnitRepository;
import com.example.fams.services.impl.UnitServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @InjectMocks
    private UnitServiceImpl unitService;

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
    void findById_ExistingUnit() {
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
    void findById_NotExistingUnit() {
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
        unitDTO.setDuration(1);
        unitDTO.setStatus(true);

        Unit savedUnit = new Unit(); // Tạo một đối tượng Unit đã được lưu thành công
        savedUnit.setId(1L);
        savedUnit.setName("Test Unit");
        savedUnit.setDuration(1);
        savedUnit.setStatus(true);

        // Mock behavior của unitRepository.save(any())
        when(unitRepository.save(any())).thenReturn(savedUnit);

        // Mock behavior của genericConverter.toDTO(any(Unit.class), eq(UnitDTO.class))
        when(genericConverter.toDTO(any(Unit.class), eq(UnitDTO.class))).thenReturn(unitDTO);

        // Act
        ResponseEntity<?> responseEntity = unitService.save(unitDTO);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
        // Add more assertions as needed
        assertEquals(unitDTO.getName(), ((UnitDTO) responseDTO.getContent()).getName());
        assertEquals(unitDTO.getDuration(), ((UnitDTO) responseDTO.getContent()).getDuration());
        assertEquals(unitDTO.getStatus(), ((UnitDTO) responseDTO.getContent()).getStatus());
    }


    @Test
    void testCreateNewUnit_ReturnSyllabusIdsNotExist() {
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setSyllabusId(1L); // Assuming syllabusId 1 doesn't exist

        // Using Mockito.lenient() to avoid UnnecessaryStubbingException
        Mockito.lenient().when(syllabusRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        CustomValidationException exception = assertThrows(CustomValidationException.class,
                () -> unitService.save(unitDTO));
        assertEquals("Syllabus with id 1 does not exist", exception.getMessage());

    }

    @Test
    void testUpdateUnit_returnSuccess() {
        Unit unit = new Unit();
        unit.setId(1L);

        // Giả mạo các phương thức cần thiết
        Mockito.lenient().when(unitRepository.findById((Long) any())).thenReturn(unit);
        when(unitRepository.save(any())).thenReturn(new Unit());
        when(genericConverter.toDTO(any(Unit.class), eq(UnitDTO.class)))
                .thenReturn(new UnitDTO());

        // Kiểm thử chức năng cập nhật
        ResponseEntity<?> responseEntity = unitService.save(new UnitDTO());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        // Kiểm tra phản hồi
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









}
