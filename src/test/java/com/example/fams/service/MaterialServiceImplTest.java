package com.example.fams.service;

import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.MaterialDTO;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.Material;
import com.example.fams.repository.MaterialRepository;
import com.example.fams.services.impl.MaterialServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaterialServiceImplTest {
    @Mock
    private MaterialRepository materialRepository;
    @Mock
    private GenericConverter genericConverter;
    @InjectMocks
    private MaterialServiceImpl materialService;

    @Test
    void findAllByStatusTrue_Success(){
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        // * Tạo mock data thay thế database
        List<Material> entities = new ArrayList<>();
        entities.add(new Material());
        entities.add(new Material());
        when(materialRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
        when(materialRepository.countAllByStatusIsTrue()).thenReturn(2L);
        when(genericConverter.toDTO(any(Material.class), eq(MaterialDTO.class)));
        ResponseEntity<?> response = materialService.findAllByStatusTrue(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseDTO != null;
        assertEquals(2, ((List<?>) responseDTO.getContent()).size());
        verify(materialRepository, times(1)).findAllByStatusIsTrue(pageable);
        verify(materialRepository, times(1)).countAllByStatusIsTrue();
        verify(genericConverter, times(entities.size())).toDTO(any(Material.class),
                eq(MaterialDTO.class));
    }


    @Test
    void findAllByStatusTrue_EmptyResult() {
        // Arrange
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        // * Tạo mock data thay thế database
        List<Material> materialList = new ArrayList<>();
        when(materialRepository.findAllByStatusIsTrue(pageable)).thenReturn(materialList);
        when(materialRepository.countAllByStatusIsTrue()).thenReturn(0L);
        ResponseEntity<?> response= materialService.findAllByStatusTrue(page,limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assert responseDTO != null;
        assertEquals(0,((List<?>)responseDTO.getContent()).size());
        verify(materialRepository, times(1)).findAllByStatusIsTrue(pageable);
        verify(materialRepository, times(1)).countAllByStatusIsTrue();
    }
    @Test
    void testFindById_ExistingMaterial() {
        // * Tạo mock data thay thế database
        Long id = 1L;
        Material material=new Material();
        material.setId(id);
        material.setStatus(true);
        MaterialDTO materialDTO=new MaterialDTO();
        materialDTO.setId(id);
        // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
        when(materialRepository.findByStatusIsTrueAndId(id)).thenReturn(material);
        when(genericConverter.toDTO(material, MaterialDTO.class)).thenReturn(materialDTO);

        // * Test hàm trong service
        ResponseEntity<?> response = materialService.findById(id);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        MaterialDTO result = (MaterialDTO) responseDTO.getContent();

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertEquals(id, result.getId());
    }

    @Test
    void testFindById_NonExistingMaterial() {
        // Arrange
        Long id = 1L;
        when(materialRepository.findByStatusIsTrueAndId(id)).thenReturn(null);
        // * Test hàm trong service
        ResponseEntity<?> response = materialService.findById(id);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Material not found", responseDTO.getDetails().get(0));

}}
