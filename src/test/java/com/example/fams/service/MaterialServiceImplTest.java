package com.example.fams.service;

import com.example.fams.config.CustomValidationException;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.MaterialDTO;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.entities.Material;
import com.example.fams.repository.MaterialRepository;
import com.example.fams.repository.SyllabusRepository;
import com.example.fams.services.impl.MaterialServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class MaterialServiceImplTest {
    @Mock
    private MaterialRepository materialRepository;
    @Mock
    private SyllabusRepository syllabusRepository;
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
    }

    // doi fix, chua hoan thien

    @Test
    void testCreateNewMaterial_returnSuccess(){
        MaterialDTO materialDTO = new MaterialDTO();
        materialDTO.setName("Material");
        materialDTO.setDescription("Description");
        materialDTO.setStatus(true);
        materialDTO.setUrl("https://fams-fsa.s3.ap-southeast-2.amazonaws.com/1712022287262_images.png");
        materialDTO.setSyllabusIds(null);
        // Mock behavior
        when(materialRepository.save(any())).thenReturn(new Material());
        when(genericConverter.toDTO(any(Material.class), eq(MaterialDTO.class)))
                .thenReturn(new MaterialDTO());

        // Act
        ResponseEntity<?> responseEntity = materialService.save(materialDTO);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
    }
    @Test
    void testCreateNewMaterial_ReturnSyllabusIdsNotExist() {
    MaterialDTO materialDTO = new MaterialDTO();
    materialDTO.setName("Material");
    materialDTO.setDescription("Description");
    materialDTO.setStatus(true);
    materialDTO.setUrl("https://fams-fsa.s3.ap-southeast-2.amazonaws.com/1712022287262_images.png");
    materialDTO.setSyllabusIds(Collections.singletonList(1L));
    Mockito.lenient().when(syllabusRepository.existsById(1L)).thenReturn(false);
    CustomValidationException exception = assertThrows(CustomValidationException.class,
                () -> materialService.save(materialDTO));
    assertEquals("Syllabus with id 1 does not exist", exception.getMessage());
    }
    @Test
    void testUpdateMaterial_returnSuccess(){
        Material material=new Material();
        material.setId(1L);
        MaterialDTO materialDTO=new MaterialDTO();
        materialDTO.setDescription("This is an update");
        materialDTO.setName("test update");
        Mockito.lenient().when((materialRepository.findById(anyLong()))).thenReturn(material);
        when(materialRepository.save(any(Material.class))).thenReturn(material);
        when(genericConverter.toDTO(any(Material.class), eq(MaterialDTO.class))).thenReturn(materialDTO);
        ResponseEntity<?> responseEntity=materialService.save(materialDTO);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
    }
    @Test
    void testChangeStatus_MaterialNotFound(){
        Long id=1L;
        when(materialRepository.findById(id)).thenReturn(null);
        ResponseEntity<?> responseEntity=materialService.changeStatus(id);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Material not found", ((ResponseDTO)responseEntity.getBody()).getDetails().get(0));
        assertEquals("Cannot change status of non-existing Material", (responseDTO.getMessage()));
        verify(materialRepository, never()).save(any()); // Ensure save is not called
    }
    @Test
    void testChangeStatus_StatusChangedSuccessfully(){
        Long id=1L;
        Material material=new Material();
        material.setId(id);
        material.setStatus(true); // Initial status is true
        when(materialRepository.findById(id)).thenReturn(material);
        ResponseEntity<?> responseEntity=materialService.changeStatus(id);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Status changed successfully", ((ResponseDTO)responseEntity.getBody()).getDetails().get(0));
        assertEquals(false, material.getStatus()); // Ensure status is toggled
        verify(materialRepository, times(1)).save(material); // Verify that save is called once
    }
}
