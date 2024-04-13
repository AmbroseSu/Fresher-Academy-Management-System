package com.example.fams.service;

import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.dto.TrainingProgramDTO;
import com.example.fams.dto.UnitDTO;
import com.example.fams.dto.request.DeleteReplace;
import com.example.fams.entities.Syllabus;
import com.example.fams.entities.SyllabusTrainingProgram;
import com.example.fams.entities.TrainingProgram;
import com.example.fams.entities.Unit;
import com.example.fams.repository.SyllabusRepository;
import com.example.fams.repository.SyllabusTrainingProgramRepository;
import com.example.fams.repository.TrainingProgramRepository;
import com.example.fams.services.ServiceUtils;
import com.example.fams.services.impl.TrainingProgramServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.apache.poi.ss.util.CellUtil.createCell;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TrainingProgramServiceImplTest {
    @Mock
    private TrainingProgramRepository trainingProgramRepository;
    @Mock
    private GenericConverter genericConverter;
    @Mock
    private SyllabusTrainingProgramRepository syllabusTrainingProgramRepository;
    @Mock
    private SyllabusRepository syllabusRepository;

    @Mock
    private ServiceUtils serviceUtils;

    @Spy
    @InjectMocks
    private TrainingProgramServiceImpl trainingProgramService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void findAllByStatusTrue_Success() {
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        List<TrainingProgram> entities = new ArrayList();
        entities.add(new TrainingProgram());
        entities.add(new TrainingProgram());


        when(trainingProgramRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
        when(trainingProgramRepository.countAllByStatusIsTrue()).thenReturn(2L);
        when(genericConverter.toDTO(any(TrainingProgram.class), eq(TrainingProgramDTO.class)))
                .thenReturn(new TrainingProgramDTO());


        ResponseEntity<?> response = trainingProgramService.findAllByStatusTrue(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

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

        // * Tạo mock data thay thế database
        List<TrainingProgram> entities = new ArrayList<>();

        // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
        when(trainingProgramRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
        when(trainingProgramRepository.countAllByStatusIsTrue()).thenReturn(0L);

        // * Test hàm trong service
        ResponseEntity<?> response = trainingProgramService.findAllByStatusTrue(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert responseDTO != null;
        assertEquals(0, ((List<?>) responseDTO.getContent()).size());
    }

    @Test
    void testFindAll() {
        // Arrange
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<TrainingProgram> trainingPrograms = new ArrayList<>();
        trainingPrograms.add(new TrainingProgram());
        trainingPrograms.add(new TrainingProgram());
        Page<TrainingProgram> page1 = new PageImpl<>(trainingPrograms);
        when(trainingProgramRepository.findAll(pageable)).thenReturn(page1);
        when(trainingProgramRepository.count()).thenReturn(2L);
        when(genericConverter.toDTO(any(TrainingProgram.class), eq(TrainingProgramDTO.class))).thenReturn(new TrainingProgramDTO());

        // Act
        ResponseEntity<?> response = trainingProgramService.findAll(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        List<TrainingProgramDTO> result = (List<TrainingProgramDTO>) responseDTO.getContent();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, result.size());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));

    }

    @Test
    void testFindAll_Exception() {
        int page = 1;
        int limit = 10;
        String errorMessage = "Mocked exception message";

        when(trainingProgramRepository.findAll(any(PageRequest.class))).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> responseEntity = trainingProgramService.findAll(page, limit);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("An error occurred while fetching training programs: " + errorMessage, responseEntity.getBody());
    }

    @Test
    void testFindById_ExistingTrainingProgram() {
        // * Tạo mock data thay thế database
        Long id = 1L;
        TrainingProgram trainingProgram = new TrainingProgram();
        trainingProgram.setId(id);
        trainingProgram.setStatus(true);

        TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
        trainingProgramDTO.setId(id);

        // * Mock các method trong service ( nghĩa là khi chạy các hàm này th return ra giá trị mock data )
        when(trainingProgramRepository.findByStatusIsTrueAndId(id)).thenReturn(trainingProgram);
        when(genericConverter.toDTO(trainingProgram, TrainingProgramDTO.class)).thenReturn(trainingProgramDTO);

        // * Test hàm trong service
        ResponseEntity<?> response = trainingProgramService.findById(id);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        TrainingProgramDTO result = (TrainingProgramDTO) responseDTO.getContent();

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertEquals(id, result.getId());
//        assertEquals(1,1);
    }

    @Test
    void testFindById_NonExistingTrainingProgram() {
        // Arrange
        Long id = 1L;
        when(trainingProgramRepository.findByStatusIsTrueAndId(id)).thenReturn(null);

        // * Test hàm trong service
        ResponseEntity<?> response = trainingProgramService.findById(id);

        // * Kiểm tra kết quả trả về
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Training program not found with ID: " + id, response.getBody());
    }

    @Test
    void testFindById_Exception() {
        Long id = 1L;
        String errorMessage = "Mocked exception message";

        // Mock the behavior of the repository to throw an exception
        when(trainingProgramRepository.findByStatusIsTrueAndId(id)).thenThrow(new RuntimeException(errorMessage));

        // Invoke the method
        ResponseEntity<?> responseEntity = trainingProgramService.findById(id);

        // Assert the status code
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        // Assert the response body contains the error message
        assertEquals("An error occurred while fetching training program: " + errorMessage, responseEntity.getBody());
    }


    // ham nay sai
    @Test
    void testCreateNewTrainingProgram_returnSuccess() {
        TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
        // Mock behavior
        when(trainingProgramRepository.save(any())).thenReturn(new TrainingProgram());
        when(genericConverter.toDTO(any(TrainingProgram.class), eq(TrainingProgramDTO.class)))
                .thenReturn(new TrainingProgramDTO());

        // Act
        ResponseEntity<?> responseEntity = trainingProgramService.save(trainingProgramDTO);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // Add more assertions as needed
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
    }

    @Test
    void testSave_SyllabusIdsNotExist() {
        TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
        trainingProgramDTO.setSyllabusIds(Collections.singletonList(1L)); // Assuming syllabusId 1 doesn't exist

        Mockito.lenient().when(syllabusRepository.existsById(1L)).thenReturn(false);

        CustomValidationException exception = assertThrows(CustomValidationException.class,
                () -> trainingProgramService.save(trainingProgramDTO));
        assertEquals("Syllabus with id [1] does not exist", exception.getMessage());
    }

    @Test
    void testCheckExist_TrainingProgramExists() {
        Long id = 1L;
        TrainingProgram trainingProgram = new TrainingProgram();
        trainingProgram.setId(id);

        when(trainingProgramRepository.findOneById(id)).thenReturn(trainingProgram);

        Boolean result = trainingProgramService.checkEixst(id);

        assertTrue(result);
    }

    @Test
    void testCheckExist_TrainingProgramDoesNotExist() {
        Long id = 1L;
        when(trainingProgramRepository.findOneById(id)).thenReturn(null);

        Boolean result = trainingProgramService.checkEixst(id);

        assertFalse(result);
    }


    @Test
    void testParseCsvFile_ValidFile() throws IOException {
        // Mock CSV file content
        String csvContent = "Name,Duration,TrainingStatus,SyllabusIds\n" +
                "Program 1,10,1,1/2\n" +
                "Program 2,20,0,null\n";

        MockMultipartFile csvFile = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        // Call the method
        List<TrainingProgramDTO> trainingProgramList = trainingProgramService.parseCsvFile(csvFile);

        // Assert the parsed training programs
        assertEquals(2, trainingProgramList.size());

        TrainingProgramDTO program1 = trainingProgramList.get(0);
        assertEquals("Program 1", program1.getName());
        assertEquals(10L, program1.getDuration());
        assertEquals(1, program1.getTraining_status());
        assertEquals(Arrays.asList(1L, 2L), program1.getSyllabusIds());

        TrainingProgramDTO program2 = trainingProgramList.get(1);
        assertEquals("Program 2", program2.getName());
        assertEquals(20L, program2.getDuration());
        assertEquals(0, program2.getTraining_status());
        assertNull(program2.getSyllabusIds());
    }

    @Test
    void testParseCsvFile_EmptyFile() throws IOException {
        // Mock empty CSV file
        MockMultipartFile csvFile = new MockMultipartFile("file", "test.csv", "text/csv", new byte[0]);

        // Call the method
        List<TrainingProgramDTO> trainingProgramList = trainingProgramService.parseCsvFile(csvFile);

        // Assert that the list is empty
        assertTrue(trainingProgramList.isEmpty());
    }

    @Test
    void testParseCsvFile_InvalidFormat() throws IOException {
        // Mock CSV file with invalid format
        String csvContent = "Name,Duration,TrainingStatus,SyllabusIds\n" +
                "Program 1,invalid,1,1/2\n";

        MockMultipartFile csvFile = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        // Call the method
        List<TrainingProgramDTO> trainingProgramList = trainingProgramService.parseCsvFile(csvFile);

        // Assert that the list is empty due to the exception
        assertTrue(trainingProgramList.isEmpty());
    }


    @Test
    void testUpdateTrainingProgram_returnSuccess() {
        // Arrange
        Long trainingProgramId = 1L;
        Long syllabusIds = 2L;

        TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
        trainingProgramDTO.setId(trainingProgramId);
        trainingProgramDTO.setSyllabusIds(Collections.singletonList(syllabusIds));

        TrainingProgram oldEntity = new TrainingProgram();
        oldEntity.setId(trainingProgramId);

        TrainingProgram updatedEntity = new TrainingProgram();
        updatedEntity.setId(trainingProgramId);

        Syllabus syllabus = new Syllabus();
        syllabus.setId(syllabusIds);

        // Mock the Authentication object
        Authentication authentication = Mockito.mock(Authentication.class);
        // Mock the getName() method to return a dummy username
        Mockito.when(authentication.getName()).thenReturn("testUser");
        // Set the mock Authentication object to the SecurityContext
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mocking necessary methods
        when(trainingProgramRepository.findOneById(trainingProgramId)).thenReturn(oldEntity);
        when(syllabusRepository.findOneById(syllabusIds)).thenReturn(syllabus);
        when(trainingProgramRepository.save(any(TrainingProgram.class))).thenReturn(updatedEntity);
        when(genericConverter.toDTO(updatedEntity, TrainingProgramDTO.class)).thenReturn(trainingProgramDTO);

        // Act
        ResponseEntity<?> responseEntity = trainingProgramService.save(trainingProgramDTO);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Saved successfully", responseDTO.getDetails().get(0));
        // Clear the SecurityContextHolder after the test
        SecurityContextHolder.clearContext();
    }



    @Test
    public void testChangeStatusWhenEntityExists() {
        Long id = 1L;
        TrainingProgram entity = new TrainingProgram();
        entity.setId(id);
        entity.setStatus(false); // Assuming default status is false

        when(trainingProgramRepository.findOneById(id)).thenReturn(entity);

        ResponseEntity<?> responseEntity = trainingProgramService.changeStatus(id);

        verify(trainingProgramRepository, times(1)).findOneById(id);
        verify(trainingProgramRepository, times(1)).save(entity);

        // Asserting the status change
        assert (entity.getStatus() == true); // Asserting that the status has changed to true
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

//    @Test
//     void testChangeStatus_TrainingProgramNotFound() {
//        // Given
//        Long id = 1L;
//        when(trainingProgramRepository.findOneById(id)).thenReturn(null);
//
//        // When
//        ResponseEntity<?> responseEntity = trainingProgramService.changeStatus(id);
//        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
//        // Then
//        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
//        assertEquals("Training program not found", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
//        assertEquals("Cannot change status of non-existing training program", (responseDTO.getMessage()));
//        verify(trainingProgramRepository, never()).save(any()); // Ensure save is not called
//    }
//

    @Test
    void testChangeStatusforUpload_ChangeStatusByName_Success() {
        // Arrange
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L, 2L));
        Boolean id = false;
        Boolean name = true;

        TrainingProgram entity1 = new TrainingProgram();
        entity1.setId(1L);
        entity1.setName("Program 1");
        entity1.setStatus(true);

        TrainingProgram entity2 = new TrainingProgram();
        entity2.setId(2L);
        entity2.setName("Program 1");
        entity2.setStatus(true);

        TrainingProgram entity3 = new TrainingProgram();
        entity3.setId(3L);
        entity3.setName("Program 1");
        entity3.setStatus(true);

        when(trainingProgramRepository.findOneById(1L)).thenReturn(entity1);
        when(trainingProgramRepository.findOneById(2L)).thenReturn(entity2);
        when(trainingProgramRepository.getAllTrainingProgramByName("Program 1")).thenReturn(Arrays.asList(entity1, entity2, entity3));

        // Act
        ResponseEntity<?> response = trainingProgramService.changeStatusforUpload(ids, id, name);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Delete Susscessfully", ((ResponseDTO) response.getBody()).getDetails().get(0));

        verify(trainingProgramRepository, times(2)).save(any(TrainingProgram.class));
    }

    @Test
    void testChangeStatusforUpload_ChangeStatusById_Success() {
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L, 2L));
        Boolean id = false;
        Boolean name = true;

        TrainingProgram entity1 = new TrainingProgram();
        entity1.setId(1L);
        entity1.setName("Program 1");
        entity1.setStatus(true);

        TrainingProgram entity2 = new TrainingProgram();
        entity2.setId(2L);
        entity2.setName("Program 1");
        entity2.setStatus(true);

        TrainingProgram entity3 = new TrainingProgram();
        entity3.setId(3L);
        entity3.setName("Program 1");
        entity3.setStatus(true);

        when(trainingProgramRepository.findOneById(1L)).thenReturn(entity1);
        when(trainingProgramRepository.findOneById(2L)).thenReturn(entity2);
        when(trainingProgramRepository.getAllTrainingProgramById(1L)).thenReturn(Collections.singletonList(entity1));
        when(trainingProgramRepository.getAllTrainingProgramById(2L)).thenReturn(Collections.singletonList(entity2));

        // Act
        ResponseEntity<?> response = trainingProgramService.changeStatusforUpload(ids, id, name);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Delete Susscessfully", ((ResponseDTO) response.getBody()).getDetails().get(0));

        verify(trainingProgramRepository, times(2)).save(any(TrainingProgram.class));
    }


    @Test
    void testChangeStatusforUpload_RemoveAllTrainingPrograms_Failure() {
        // Arrange
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L, 2L));
        Boolean id = true;
        Boolean name = true;

        TrainingProgram entity1 = new TrainingProgram();
        entity1.setId(1L);
        entity1.setName("Program 1");
        entity1.setStatus(true);

        TrainingProgram entity2 = new TrainingProgram();
        entity2.setId(2L);
        entity2.setName("Program 1");
        entity2.setStatus(true);

        when(trainingProgramRepository.findOneById(1L)).thenReturn(entity1);
        when(trainingProgramRepository.findOneById(2L)).thenReturn(entity2);
        when(trainingProgramRepository.getAllSyllabusByNameAndId(1L, "Program 1")).thenReturn(Arrays.asList(entity1, entity2));
        when(trainingProgramRepository.getAllSyllabusByNameAndId(2L, "Program 1")).thenReturn(Arrays.asList(entity1, entity2));

        // Act
        ResponseEntity<?> response = trainingProgramService.changeStatusforUpload(ids, id, name);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Don't remove all, leave one id", ((ResponseDTO) response.getBody()).getMessage());

        verify(trainingProgramRepository, never()).save(any(TrainingProgram.class));
    }

    @Test
    void testChangeStatusforUpload_RemoveAllTrainingProgramsByName_Failure() {
        // Arrange
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L, 2L));
        Boolean id = false;
        Boolean name = true;

        TrainingProgram entity1 = new TrainingProgram();
        entity1.setId(1L);
        entity1.setName("Program 1");
        entity1.setStatus(true);

        TrainingProgram entity2 = new TrainingProgram();
        entity2.setId(2L);
        entity2.setName("Program 1");
        entity2.setStatus(true);

        when(trainingProgramRepository.findOneById(1L)).thenReturn(entity1);
        when(trainingProgramRepository.findOneById(2L)).thenReturn(entity2);
        when(trainingProgramRepository.getAllTrainingProgramByName("Program 1")).thenReturn(Arrays.asList(entity1, entity2));

        // Act
        ResponseEntity<?> response = trainingProgramService.changeStatusforUpload(ids, id, name);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Don't remove all, leave one id", ((ResponseDTO) response.getBody()).getMessage());

        verify(trainingProgramRepository, never()).save(any(TrainingProgram.class));
    }

    @Test
    void testChangeStatusforUpload_RemoveAllTrainingProgramsById_Failure() {
        // Arrange
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L, 2L));
        Boolean id = true;
        Boolean name = false;

        TrainingProgram entity1 = new TrainingProgram();
        entity1.setId(1L);
        entity1.setName("Program 1");
        entity1.setStatus(true);

        TrainingProgram entity2 = new TrainingProgram();
        entity2.setId(1L);
        entity2.setName("Program 2");
        entity2.setStatus(true);

        when(trainingProgramRepository.findOneById(1L)).thenReturn(entity1);
        when(trainingProgramRepository.findOneById(2L)).thenReturn(entity2);
        when(trainingProgramRepository.getAllTrainingProgramById(1L)).thenReturn(Arrays.asList(entity1, entity2));

        // Act
        ResponseEntity<?> response = trainingProgramService.changeStatusforUpload(ids, id, name);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Don't remove all, leave one id", ((ResponseDTO) response.getBody()).getMessage());

        verify(trainingProgramRepository, never()).save(any(TrainingProgram.class));
    }


    @Test
    void testSearchSortFilter() {
        // Arrange
        TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
        trainingProgramDTO.setName("Test Training Program");
        trainingProgramDTO.setStartTime(1618959600000L); // April 18, 2021 00:00:00 UTC
        trainingProgramDTO.setDuration(3600000L); // 1 hour
        trainingProgramDTO.setTraining_status(1);

        String sortByCreatedDate = "asc";
        int page = 1;
        int limit = 10;

        List<TrainingProgram> entities = new ArrayList<>();
        entities.add(new TrainingProgram());
        entities.add(new TrainingProgram());
        when(genericConverter.toDTO(any(TrainingProgram.class), eq(TrainingProgramDTO.class))).thenReturn(trainingProgramDTO);
        when(trainingProgramRepository.searchSortFilter(
                trainingProgramDTO.getName(),
                trainingProgramDTO.getStartTime(),
                trainingProgramDTO.getTraining_status(),
                sortByCreatedDate,
                PageRequest.of(page - 1, limit)
        )).thenReturn(entities);

        // Act
        ResponseEntity<?> response = trainingProgramService.searchSortFilter(trainingProgramDTO, sortByCreatedDate, page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        verify(trainingProgramRepository, times(1)).searchSortFilter(
                trainingProgramDTO.getName(),
                trainingProgramDTO.getStartTime(),
                trainingProgramDTO.getTraining_status(),
                sortByCreatedDate,
                PageRequest.of(page - 1, limit)
        );
    }

    @Test
    void testChangeStatusforUpload_NonExistingTrainingProgram_Failure() {
        // Arrange
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L, 2L));
        Boolean id = true;
        Boolean name = true;

        when(trainingProgramRepository.findOneById(1L)).thenReturn(null);
        when(trainingProgramRepository.findOneById(2L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = trainingProgramService.changeStatusforUpload(ids, id, name);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Cannot change status of non-existing Syllabus", ((ResponseDTO) response.getBody()).getMessage());

        verify(trainingProgramRepository, never()).save(any(TrainingProgram.class));
    }

    @Test
    void testSearchSortFilterADMIN() {
        // Arrange
        TrainingProgramDTO trainingProgramDTO = new TrainingProgramDTO();
        trainingProgramDTO.setName("Test Training Program");
        trainingProgramDTO.setStartTime(1618959600000L); // April 18, 2021 00:00:00 UTC
        trainingProgramDTO.setDuration(3600000L); // 1 hour
        trainingProgramDTO.setTraining_status(1);

        String sortById = "asc";
        int page = 1;
        int limit = 10;

        List<TrainingProgram> entities = new ArrayList<>();
        entities.add(new TrainingProgram());
        entities.add(new TrainingProgram());
        when(genericConverter.toDTO(any(TrainingProgram.class), eq(TrainingProgramDTO.class))).thenReturn(trainingProgramDTO);
        when(trainingProgramRepository.searchSortFilterADMIN(
                trainingProgramDTO.getName(),
                trainingProgramDTO.getStartTime(),
                trainingProgramDTO.getTraining_status(),
                sortById,
                PageRequest.of(page - 1, limit)
        )).thenReturn(entities);

        // Act
        ResponseEntity<?> response = trainingProgramService.searchSortFilterADMIN(trainingProgramDTO, sortById, page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        verify(trainingProgramRepository, times(1)).searchSortFilterADMIN(
                trainingProgramDTO.getName(),
                trainingProgramDTO.getStartTime(),
                trainingProgramDTO.getTraining_status(),
                sortById,
                PageRequest.of(page - 1, limit)
        );
    }

    @Test
     void testCheckCsvFile_Success() throws IOException {
        // Mock CSV file content
        String csvContent = "Name,Duration,TrainingStatus,SyllabusIds\n" +
                "Program 1,10,1,1/2\n" +
                "Program 2,20,0,null\n";

        MockMultipartFile csvFile = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        // Call the method
        ResponseEntity<?> responseEntity = trainingProgramService.checkCsvFile(csvFile);

        // Assert the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the content of the response
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(2, ((List<?>) responseDTO.getContent()).size());

    }

    @Test
    void testCheckCsvFile_InvalidSyllabusIds() throws IOException {
        // Mock CSV file content with invalid syllabusIds
        String csvContent = "Name,Duration,TrainingStatus,SyllabusIds\n" +
                "Program 1,10,1,1/invalid\n" +
                "Program 2,20,0,null\n";

        MockMultipartFile csvFile = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        ResponseEntity<?> responseEntity = trainingProgramService.checkCsvFile(csvFile);
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        // Assert the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        assertEquals("Please check format file", responseDTO.getDetails().get(0));
        assertTrue(responseDTO.getMessage().startsWith("Invalid format for syllabusIds:"));
    }

    @Test
    void checkTrainingProgramReplace_ValidFile_ReturnsSavedSuccessfully() throws IOException {
        // Mocking file and other necessary objects
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "filecontent".getBytes());
        when(trainingProgramService.checkCsvFile(file)).thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        // Calling the method to be tested
        ResponseEntity<?> responseEntity = trainingProgramService.checkTrainingProgramReplace(file, false, true);

        // Asserting the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Saved successfully", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));

    }

    @Test
    void checkTrainingProgramReplace_InvalidFileFormat_ReturnsBadRequest() throws IOException {
        // Mocking an invalid file format
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "filecontent".getBytes());
        when(trainingProgramService.checkCsvFile(file)).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        // Calling the method to be tested
        ResponseEntity<?> responseEntity = trainingProgramService.checkTrainingProgramReplace(file, false, true);

        // Asserting the response
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Please check format file", ((ResponseDTO) responseEntity.getBody()).getDetails().get(0));
    }

    @Test
    void testCheckTrainingProgramSkip_NameTrueIdFalse_Success() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        InputStream inputStream = new ByteArrayInputStream("test data".getBytes());
        when(file.getInputStream()).thenReturn(inputStream);

        Boolean id = false;
        Boolean name = true;

        TrainingProgramDTO dto1 = new TrainingProgramDTO();
        dto1.setName("Program 1");
        TrainingProgramDTO dto2 = new TrainingProgramDTO();
        dto2.setName("Program 2");

        when(trainingProgramService.checkCsvFile(file)).thenReturn(ResponseEntity.ok().build());
        when(trainingProgramService.parseCsvFile(file)).thenReturn(Arrays.asList(dto1, dto2));
        when(trainingProgramRepository.getAllTrainingProgramByName("Program 1")).thenReturn(Collections.emptyList());
        when(trainingProgramRepository.getAllTrainingProgramByName("Program 2")).thenReturn(Collections.emptyList());
        when(genericConverter.toDTO(any(TrainingProgram.class), eq(TrainingProgramDTO.class)))
                .thenReturn(new TrainingProgramDTO());
        // Act
        ResponseEntity<?> response = trainingProgramService.checkTrainingProgramSkip(file, id, name);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Saved successfully", ((ResponseDTO) response.getBody()).getDetails().get(0));

        verify(trainingProgramService, times(2)).save(any(TrainingProgramDTO.class));
    }

}

