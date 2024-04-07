package com.example.fams.service;

import com.example.fams.config.CustomValidationException;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.MaterialDTO;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.entities.Material;
import com.example.fams.entities.Syllabus;
import com.example.fams.repository.MaterialRepository;
import com.example.fams.repository.SyllabusMaterialRepository;
import com.example.fams.repository.SyllabusRepository;
import com.example.fams.services.IStorageService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private SyllabusMaterialRepository syllabusMaterialRepository;
    @Mock
    private IStorageService storageService;
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
        when(genericConverter.toDTO(any(Material.class), eq(MaterialDTO.class))).thenReturn(new MaterialDTO());
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
    assertEquals("Syllabus with id [1] does not exist", exception.getMessage());
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
    @Test
    public void testSearchSortFilterByName() {
        // Mock search data
        String searchName = "Test Material";
        String description = null; // Not searching by description in this test
        int page = 0;
        int limit = 10;
        Pageable pageable = PageRequest.of(page, limit);
        List<Material> expectedMaterials = new ArrayList<>(); // Replace with expected results

        // Mock repository behavior
        when(materialRepository.searchSortFilter(searchName, description, pageable)).thenReturn(expectedMaterials);
        when(materialRepository.countSearchSortFilter(searchName, description)).thenReturn(1L); // Expected count

        // Call the method
        MaterialDTO searchDTO = new MaterialDTO();
        searchDTO.setName(searchName);
        ResponseEntity<?> response = materialService.searchSortFilter(searchDTO, page + 1, limit);

        // Verify results
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Additional assertions can be made here to verify response content based on expectedMaterials
    }
    @Test
    public void testSearchSortFilterADMINByName_ReturnSuccess() {
        // Mock search data
        String searchName = "Test Material";
        String description = null; // Not searching by description in this test
        String sortById = "asc"; // Sorting order (ascending)
        int page = 0;
        int limit = 10;
        Pageable pageable = PageRequest.of(page, limit);
        List<Material> expectedMaterials = new ArrayList<>(); // Replace with expected results

        // Mock repository behavior
        when(materialRepository.searchSortFilterADMIN(searchName, description, sortById, pageable)).thenReturn(expectedMaterials);
        when(materialRepository.countSearchSortFilter(searchName, description)).thenReturn(1L); // Expected count

        // Call the method
        MaterialDTO searchDTO = new MaterialDTO();
        searchDTO.setName(searchName);
        ResponseEntity<?> response = materialService.searchSortFilterADMIN(searchDTO, sortById, page + 1, limit);

        // Verify results
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Additional assertions can be made here to verify response content based on expectedMaterials
    }
    @Test
    void testFindAll_Success() {
        // Mock page and limit
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        // Mock data for the repository to return
        List<Material> entities = new ArrayList<>();
        entities.add(new Material());
        entities.add(new Material());
        when(materialRepository.findAllByOrderByIdDesc(pageable)).thenReturn(entities);
        when(materialRepository.count()).thenReturn(2L);

        // Mock conversion from entity to DTO
        when(genericConverter.toDTO(any(Material.class), eq(MaterialDTO.class))).thenReturn(new MaterialDTO());

        // Call the findAll method
        ResponseEntity<?> response = materialService.findAll(page, limit);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        assert responseDTO != null;
        List<?> content = (List<?>) responseDTO.getContent();
        assertEquals(2, content.size());

        // Verify method invocations
        verify(materialRepository, times(1)).findAllByOrderByIdDesc(pageable);
        verify(materialRepository, times(1)).count();
        verify(genericConverter, times(entities.size())).toDTO(any(Material.class), eq(MaterialDTO.class));
    }
    @Test
    void testCheckExist_ExistingMaterial() {
        // Mock data
        Long materialId = 1L;
        Material material = new Material();
        when(materialRepository.findById(materialId)).thenReturn(material);

        // Call the method to test
        boolean exists = materialService.checkExist(materialId);

        // Assert
        assertTrue(exists);
    }
    @Test
    void testUpdateMaterial_Success() {
        // Mock the Authentication object
        Authentication authentication = Mockito.mock(Authentication.class);
        // Mock the getName() method to return a dummy username
        Mockito.when(authentication.getName()).thenReturn("testUser");
        // Set the mock Authentication object to the SecurityContext
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        // Mock data
        Long materialId = 1L;
        MaterialDTO materialDTO = new MaterialDTO();
        materialDTO.setId(materialId);
        materialDTO.setName("Updated Material Name");
        materialDTO.setDescription("Updated Material Description");
        materialDTO.setSyllabusIds(null);
        Material oldEntity = new Material();
        oldEntity.setId(materialId);
        oldEntity.setStatus(true);
        // Mock repository behaviors
        when(materialRepository.findById(materialId)).thenReturn(oldEntity);
        when(genericConverter.toDTO(any(Material.class), eq(MaterialDTO.class))).thenReturn(materialDTO);
        // Call the update method
        ResponseEntity<?> responseEntity = materialService.save(materialDTO);
        // Verify results
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertNotNull(responseDTO);
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
        // Verify interactions with repositories
        verify(materialRepository, times(1)).findById(materialId);
        verify(materialRepository, times(1)).save(any(Material.class));
        verify(syllabusMaterialRepository, times(1)).deleteAllByMaterialId(materialId);
        verify(materialRepository, times(1)).save(any(Material.class)); // Ensure save is called after updating entity
        // Clear the SecurityContextHolder after the test
        SecurityContextHolder.clearContext();
    }
}


