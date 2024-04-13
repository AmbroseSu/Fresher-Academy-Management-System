package com.example.fams.service;

import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.ResponseDTO;
import com.example.fams.dto.SyllabusDTO;
import com.example.fams.dto.request.DeleteReplace;
import com.example.fams.entities.*;
import com.example.fams.repository.*;
import com.example.fams.services.impl.SyllabusServiceImpl;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SyllabusServiceImplTest {

    @Mock
    private SyllabusRepository syllabusRepository;

    @Mock
    private SyllabusObjectiveRepository syllabusObjectiveRepository;

    @Mock
    private GenericConverter genericConverter;

    @Mock
    private TrainingProgramRepository trainingProgramRepository;

    @Mock
    private LearningObjectiveRepository learningObjectiveRepository;

    @Mock
    private SyllabusTrainingProgramRepository syllabusTrainingProgramRepository;

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private SyllabusMaterialRepository syllabusMaterialRepository;

    @Mock
    private UnitRepository unitRepository;
//    @Mock
//    private SyllabusServiceImpl syllabusServiceMock;
//    @Mock
//    private SyllabusRepository syllabusRepositoryMock;

    @MockBean
    private SyllabusServiceImpl syllabusServiceMockBean;

    @MockBean
    private SyllabusRepository syllabusRepositoryMockBean;

    @Spy
    @InjectMocks
    private SyllabusServiceImpl syllabusService;



    @Test
    public void testFindById() {
        // Given
        Long id = 1L;
        Syllabus syllabus = new Syllabus();
        syllabus.setId(id);
        syllabus.setStatus(true);

        // Mock phương thức convertSyllabusToSyllabusDTO
        SyllabusDTO expectedDTO = new SyllabusDTO(); // Đây là đối tượng DTO mà bạn mong muốn phương thức convertSyllabusToSyllabusDTO trả về
        when(genericConverter.toDTO(syllabus, SyllabusDTO.class)).thenReturn(expectedDTO);

        when(syllabusRepository.findByStatusIsTrueAndId(id)).thenReturn(syllabus);

        // When
        ResponseEntity<?> response = syllabusService.findById(id);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));

    }

    @Test
    public void testFindAllByStatusTrue() {
        // Given
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Syllabus> entities = new ArrayList<>();
        entities.add(new Syllabus());
        entities.add(new Syllabus());


        when(syllabusRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
        when(syllabusRepository.countAllByStatusIsTrue()).thenReturn(2L);
        when(genericConverter.toDTO(any(Syllabus.class), eq(SyllabusDTO.class)))
                .thenReturn(new SyllabusDTO());

        // When
        ResponseEntity<?> response = syllabusService.findAllByStatusTrue(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertEquals(2, ((List<?>) responseDTO.getContent()).size());
    }
    @Test
    void changeStatusTest() {
        // Arrange
        Long id = 1L;
        Syllabus syllabus = new Syllabus();
        syllabus.setId(id);
        syllabus.setStatus(true);

        when(syllabusRepository.findOneById(id)).thenReturn(syllabus);

        // Act
        ResponseEntity<?> response = syllabusService.changeStatus(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void testFindAllByStatusTrue_EmptyResult() {
        // Given
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Syllabus> entities = new ArrayList<>();
//        PageImpl<Syllabus> syllabusPage = new PageImpl<>(entities, pageable, entities.size());

        when(syllabusRepository.findAllByStatusIsTrue(pageable)).thenReturn(entities);
        when(syllabusRepository.countAllByStatusIsTrue()).thenReturn(0L);

        // When
        ResponseEntity<?> response = syllabusService.findAllByStatusTrue(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertEquals(0, ((List<?>) responseDTO.getContent()).size());
    }
    @Test
    public void testFindAll() {
        // Given
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Syllabus> entities = new ArrayList<>();
        entities.add(new Syllabus());
        entities.add(new Syllabus());
        Page<Syllabus> syllabusPage = new PageImpl<>(entities, pageable, entities.size());

        when(syllabusRepository.findAll(pageable)).thenReturn(syllabusPage);
        when(genericConverter.toDTO(any(Syllabus.class), eq(SyllabusDTO.class)))
                .thenReturn(new SyllabusDTO());

        // When
        ResponseEntity<?> response = syllabusService.findAll(page, limit);
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fetched successfully", responseDTO.getDetails().get(0));
        assertEquals(2, ((List<?>) responseDTO.getContent()).size());
    }

    @Test
    public void testSaveNewSyllabus() {
        // Given
        SyllabusDTO syllabusDTO = new SyllabusDTO();
        syllabusDTO.setUnitIds(Arrays.asList(1L, 2L));
        syllabusDTO.setTrainingProgramIds(Arrays.asList(3L, 4L));
        syllabusDTO.setLearningObjectiveIds(Arrays.asList(5L, 6L));
        syllabusDTO.setMaterialIds(Arrays.asList(7L, 8L));

        when(trainingProgramRepository.existsById(4L)).thenReturn(true);
        when(trainingProgramRepository.existsById(3L)).thenReturn(true);
        when(unitRepository.existsById(String.valueOf(1L))).thenReturn(true);
        when(unitRepository.existsById(String.valueOf(2L))).thenReturn(true);
        when(learningObjectiveRepository.existsById(5L)).thenReturn(true);
        when(learningObjectiveRepository.existsById(6L)).thenReturn(true);
        when(materialRepository.existsById(String.valueOf(7L))).thenReturn(true);
        when(materialRepository.existsById(String.valueOf(8L))).thenReturn(true);
        when(genericConverter.toDTO(any(Syllabus.class), eq(SyllabusDTO.class))).thenReturn(new SyllabusDTO());
        Syllabus newSyllabus = new Syllabus();
        when(genericConverter.toEntity(any(SyllabusDTO.class), eq(Syllabus.class))).thenReturn(newSyllabus);

        // Mock the syllabusRepository.findById method to return a non-empty Optional
        Syllabus existingSyllabus = new Syllabus();
        existingSyllabus.setId(1L);
        when(syllabusRepository.findById(1L)).thenReturn(Optional.of(existingSyllabus));

        // Mock the syllabusRepository.save method for a new Syllabus
        when(syllabusRepository.save(newSyllabus)).thenAnswer(invocation -> {
            Syllabus savedSyllabus = invocation.getArgument(0);
            savedSyllabus.setId(1L); // Set a valid ID for the saved Syllabus
            return savedSyllabus;
        });
        doNothing().when(syllabusService).loadListSyllabusObjectiveFromSyllabusId(anyList(), anyLong());
        doNothing().when(syllabusService).loadListTrainingProgramFromSyllabusId(anyList(), anyLong());
        doNothing().when(syllabusService).loadListMaterialFromSyllabusId(anyList(), anyLong());
        doNothing().when(syllabusService).loadListUnitFromListUnitIds(anyList(), anyLong());
        // When
        ResponseEntity<?> response = syllabusService.save(syllabusDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void testUpdateSyllabus() {
        // Given
        SyllabusDTO syllabusDTO = new SyllabusDTO();
        syllabusDTO.setId(1L); // Đặt id cho một Syllabus đã tồn tại
        // Set up các trường dữ liệu cần thiết cho việc cập nhật Syllabus
//        syllabusDTO.setUnitIds(Arrays.asList(1L, 2L));
//        syllabusDTO.setTrainingProgramIds(Arrays.asList(3L, 4L));
//        syllabusDTO.setLearningObjectiveIds(Arrays.asList(5L, 6L));
//        syllabusDTO.setMaterialIds(Arrays.asList(7L, 8L));

        Syllabus exitingSyllabus = new Syllabus();
        exitingSyllabus.setId(syllabusDTO.getId());

        when(syllabusRepository.save(exitingSyllabus)).thenReturn(exitingSyllabus);


        User testUser = new User();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(testUser.getUsername());


        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        // Mock các đối tượng phụ thuộc
        when(syllabusRepository.findById(syllabusDTO.getId())).thenReturn(Optional.of(exitingSyllabus));
//        when(unitRepository.findById(any(Long.class))).thenReturn(new Unit());
//        when(trainingProgramRepository.findById(any(Long.class))).thenReturn(Optional.of(new TrainingProgram()));
//        when(learningObjectiveRepository.findById(any(Long.class))).thenReturn(new LearningObjective());
//        when(materialRepository.findById(any(Long.class))).thenReturn(new Material());
        when(genericConverter.toEntity(any(SyllabusDTO.class), eq(Syllabus.class))).thenReturn(exitingSyllabus);
        when(genericConverter.toDTO(any(Syllabus.class), eq(SyllabusDTO.class))).thenReturn(syllabusDTO);
        // When
        ResponseEntity<?> response = syllabusService.save(syllabusDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Kiểm tra các chi tiết khác trong ResponseDTO
    }
    @Test
    public void testLoadListTrainingProgramFromSyllabusId_NonEmptyList() {
        // Arrange
        Long syllabusId = 1L;
        List<Long> requestTrainingProgramIds = Arrays.asList(1L, 2L, 3L);

        Syllabus syllabus = new Syllabus();
        syllabus.setId(syllabusId);

        TrainingProgram trainingProgram1 = new TrainingProgram();
        trainingProgram1.setId(1L);
        TrainingProgram trainingProgram2 = new TrainingProgram();
        trainingProgram2.setId(2L);
        TrainingProgram trainingProgram3 = new TrainingProgram();
        trainingProgram3.setId(3L);

        when(syllabusRepository.findOneById(syllabusId)).thenReturn(syllabus);
        when(trainingProgramRepository.findById(1L)).thenReturn(Optional.of(trainingProgram1));
        when(trainingProgramRepository.findById(2L)).thenReturn(Optional.of(trainingProgram2));
        when(trainingProgramRepository.findById(3L)).thenReturn(Optional.of(trainingProgram3));

        // Act
        syllabusService.loadListTrainingProgramFromSyllabusId(requestTrainingProgramIds, syllabusId);

        // Assert
        verify(syllabusRepository, times(3)).findOneById(syllabusId);
        verify(trainingProgramRepository, times(1)).findById(1L);
        verify(trainingProgramRepository, times(1)).findById(2L);
        verify(trainingProgramRepository, times(1)).findById(3L);
        verify(syllabusTrainingProgramRepository, times(3)).save(any(SyllabusTrainingProgram.class));
    }






    @Test
    public void testLoadListTrainingProgramFromSyllabusId_EmptyList() {
        // Given
        List<Long> requestTrainingProgramIds = Collections.emptyList();
        Long syllabusId = 1L;

        // When
        syllabusService.loadListTrainingProgramFromSyllabusId(requestTrainingProgramIds, syllabusId);

        // Then
        // Không cần kiểm tra gì cả
    }

    @Test
    public void testCheckExist_SyllabusExists() {
        // Given
        Long id = 1L;
        Syllabus syllabus = new Syllabus();
        when(syllabusRepository.findOneById(id)).thenReturn(syllabus);

        // When
        Boolean exists = syllabusService.checkExist(id);

        // Then
        assertTrue(exists);
    }

    @Test
    public void testCheckExist_SyllabusDoesNotExist() {
        // Given
        Long id = 1L;
        when(syllabusRepository.findOneById(id)).thenReturn(null);

        // When
        Boolean exists = syllabusService.checkExist(id);

        // Then
        assertFalse(exists);
    }

    @Test
    public void testSearchSortFilter() {
        // Given
        SyllabusDTO syllabusDTO = new SyllabusDTO();
        syllabusDTO.setName("Test Name");
        syllabusDTO.setCode("TEST-CODE");
        syllabusDTO.setDescription("Test Description");
        syllabusDTO.setIsApproved(true);
        syllabusDTO.setIsActive(true);
        syllabusDTO.setVersion("1.0");
        String sortByCreatedDate = "asc";
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        List<Syllabus> entities = Arrays.asList(new Syllabus(), new Syllabus());
        when(syllabusRepository.searchSortFilter(
                syllabusDTO.getName(),
                syllabusDTO.getCode(),
                syllabusDTO.getDescription(),
                syllabusDTO.getIsApproved(),
                syllabusDTO.getIsActive(),
                syllabusDTO.getVersion(),
                sortByCreatedDate,
                pageable
        )).thenReturn(entities);

        Long count = 2L;
        when(syllabusRepository.countSearchSortFilter(
                syllabusDTO.getName(),
                syllabusDTO.getCode(),
                syllabusDTO.getDescription(),
                syllabusDTO.getIsApproved(),
                syllabusDTO.getIsActive(),
                syllabusDTO.getVersion()
        )).thenReturn(count);

        when(genericConverter.toDTO(any(Syllabus.class), eq(SyllabusDTO.class))).thenReturn(new SyllabusDTO());

        // When
        ResponseEntity<?> response = syllabusService.searchSortFilter(syllabusDTO, sortByCreatedDate, page, limit);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Kiểm tra các chi tiết khác trong ResponseDTO
    }

    @Test
    public void testSearchSortFilterADMIN() {
        // Given
        SyllabusDTO syllabusDTO = new SyllabusDTO();
        syllabusDTO.setName("Test Name");
        syllabusDTO.setCode("TEST-CODE");
        syllabusDTO.setDescription("Test Description");
        syllabusDTO.setIsApproved(true);
        syllabusDTO.setIsActive(true);
        syllabusDTO.setVersion("1.0");
        String sortById = "asc";
        int page = 1;
        int limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);

        List<Syllabus> entities = Arrays.asList(new Syllabus(), new Syllabus());
        when(syllabusRepository.searchSortFilterADMIN(
                syllabusDTO.getName(),
                syllabusDTO.getCode(),
                syllabusDTO.getDescription(),
                syllabusDTO.getIsApproved(),
                syllabusDTO.getIsActive(),
                syllabusDTO.getVersion(),
                sortById,
                pageable
        )).thenReturn(entities);

        Long count = 2L;
        when(syllabusRepository.countSearchSortFilter(
                syllabusDTO.getName(),
                syllabusDTO.getCode(),
                syllabusDTO.getDescription(),
                syllabusDTO.getIsApproved(),
                syllabusDTO.getIsActive(),
                syllabusDTO.getVersion()
        )).thenReturn(count);

        when(genericConverter.toDTO(any(Syllabus.class), eq(SyllabusDTO.class))).thenReturn(new SyllabusDTO());

        // When
        ResponseEntity<?> response = syllabusService.searchSortFilterADMIN(syllabusDTO, sortById, page, limit);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Kiểm tra các chi tiết khác trong ResponseDTO
    }

    @Test
    public void testConvertListSyllabusToListSyllabusDTO() {
        // Given
        List<Syllabus> syllabusList = Arrays.asList(new Syllabus(), new Syllabus());
        List<SyllabusDTO> syllabusDTOs = new ArrayList<>();
        when(genericConverter.toDTO(any(Syllabus.class), eq(SyllabusDTO.class))).thenReturn(new SyllabusDTO());

        // When
        syllabusService.convertListSyllabusToListSyllabusDTO(syllabusList, syllabusDTOs);

        // Then
        assertEquals(2, syllabusDTOs.size());
    }

    @Test
    public void testConvertSyllabusToSyllabusDTO() {
        SyllabusDTO dto = new SyllabusDTO();
        dto.setId(1L);
        dto.setName("Test Syllabus");
        // Arrange
        Syllabus syllabus = new Syllabus();
        syllabus.setId(1L);
        syllabus.setName("Test Syllabus");
        // Add other properties as needed
//        when(genericConverter.toEntity(any(SyllabusDTO.class), eq(Syllabus.class))).thenReturn(syllabus);
        when(genericConverter.toDTO(any(Syllabus.class), eq(SyllabusDTO.class))).thenReturn(dto);
        // Act
        SyllabusDTO syllabusDTO = syllabusService.convertSyllabusToSyllabusDTO(syllabus);

        // Assert
        assertEquals(syllabus.getId(), syllabusDTO.getId());
        assertEquals(syllabus.getName(), syllabusDTO.getName());
        // Add other assertions as needed
    }


    @Test
    public void testParseCsvFile() throws IOException {
        // Given
        String csvData = "name,code,description,isApproved,isActive,version,attendee,unitIds,learningObjectiveIds,materialIds,trainingProgramIds\n" +
                "Test Syllabus,SYL001,This is a test syllabus,true,true,1.0,100,1,2,3,4";
        byte[] bytes = csvData.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        MultipartFile file = new MockMultipartFile("test.csv", bytes);

        // When
        List<SyllabusDTO> result = syllabusService.parseCsvFile(file);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        SyllabusDTO syllabusDTO = result.get(0);
        assertEquals("Test Syllabus", syllabusDTO.getName());
        assertEquals("SYL001", syllabusDTO.getCode());
        assertEquals("This is a test syllabus", syllabusDTO.getDescription());
        assertTrue(syllabusDTO.getIsApproved());
        assertTrue(syllabusDTO.getIsActive());
        assertEquals("1.0", syllabusDTO.getVersion());
        assertEquals(100, syllabusDTO.getAttendee());
        assertEquals(List.of(1L), syllabusDTO.getUnitIds());
        assertEquals(List.of(2L), syllabusDTO.getLearningObjectiveIds());
        assertEquals(List.of(3L), syllabusDTO.getMaterialIds());
        assertEquals(List.of(4L), syllabusDTO.getTrainingProgramIds());
    }

    @Test
    public void testCheckCsvFile() throws IOException {
        // Given
        // Prepare a sample CSV file with test data
        String csvData = "Name,Code,Description,IsApproved,IsActive,Version,Attendee,UnitIds,LearningObjectiveIds,MaterialIds,TrainingProgramIds\n" +
                "TestName,TestCode,TestDescription,true,true,1.0,100,1/2/3,4/5/6,7/8/9,10/11/12\n" +
                "TestName2,TestCode2,TestDescription2,false,false,2.0,200,4/5/6,7/8/9,10/11/12,13/14/15";
        MultipartFile file = new MockMultipartFile("test.csv", csvData.getBytes());

        // Mock the repository and service dependencies
        when(syllabusRepository.count()).thenReturn(0L);

        // When
        ResponseEntity<?> response = syllabusService.checkCsvFile(file);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // Validate the content of the response body
        ResponseDTO responseDTO = (ResponseDTO) response.getBody();
        assertNotNull(responseDTO.getContent());
        assertTrue(responseDTO.getContent() instanceof List);

        List<?> contentList = (List<?>) responseDTO.getContent();
        assertEquals(2, contentList.size());

        // Validate the content of the first SyllabusDTO object
        SyllabusDTO firstSyllabusDTO = (SyllabusDTO) contentList.get(0);
        assertEquals("TestName", firstSyllabusDTO.getName());
        assertEquals("TestCode", firstSyllabusDTO.getCode());
        assertEquals("TestDescription", firstSyllabusDTO.getDescription());
        assertTrue(firstSyllabusDTO.getIsApproved());
        assertTrue(firstSyllabusDTO.getIsActive());
        assertEquals("1.0", firstSyllabusDTO.getVersion());
        assertEquals(100L, (long) firstSyllabusDTO.getAttendee());
        assertEquals(Arrays.asList(1L, 2L, 3L), firstSyllabusDTO.getUnitIds());
        assertEquals(Arrays.asList(4L, 5L, 6L), firstSyllabusDTO.getLearningObjectiveIds());
        assertEquals(Arrays.asList(7L, 8L, 9L), firstSyllabusDTO.getMaterialIds());
        assertEquals(Arrays.asList(10L, 11L, 12L), firstSyllabusDTO.getTrainingProgramIds());

        // Validate the content of the second SyllabusDTO object
        SyllabusDTO secondSyllabusDTO = (SyllabusDTO) contentList.get(1);
        assertEquals("TestName2", secondSyllabusDTO.getName());
        assertEquals("TestCode2", secondSyllabusDTO.getCode());
        assertEquals("TestDescription2", secondSyllabusDTO.getDescription());
        assertFalse(secondSyllabusDTO.getIsApproved());
        assertFalse(secondSyllabusDTO.getIsActive());
        assertEquals("2.0", secondSyllabusDTO.getVersion());
        assertEquals(200L, (long) secondSyllabusDTO.getAttendee());
        assertEquals(Arrays.asList(4L, 5L, 6L), secondSyllabusDTO.getUnitIds());
        assertEquals(Arrays.asList(7L, 8L, 9L), secondSyllabusDTO.getLearningObjectiveIds());
        assertEquals(Arrays.asList(10L, 11L, 12L), secondSyllabusDTO.getMaterialIds());
        assertEquals(Arrays.asList(13L, 14L, 15L), secondSyllabusDTO.getTrainingProgramIds());
    }

    @Test
    public void testCheckSyllabusReplace() throws IOException {
        // Given
        // Prepare a sample CSV file with test data
        String csvData = "Name,Code,Description,IsApproved,IsActive,Version,Attendee,UnitIds,LearningObjectiveIds,MaterialIds,TrainingProgramIds\n" +
                "TestName,TestCode,TestDescription,true,true,1.0,100,1/2/3,4/5/6,7/8/9,10/11/12\n" +
                "TestName2,TestCode2,TestDescription2,false,false,2.0,200,4/5/6,7/8/9,10/11/12,13/14/15";
        MultipartFile file = new MockMultipartFile("test.csv", csvData.getBytes());

        // Mock the repository and service dependencies
        when(syllabusRepository.getAllSyllabusByName(anyString())).thenReturn(Collections.emptyList());
        when(syllabusRepository.getAllSyllabusByCode(anyString())).thenReturn(Collections.emptyList());
        when(syllabusRepository.getAllSyllabusByNameAndCode(anyString(), anyString())).thenReturn(Collections.emptyList());
        when(trainingProgramRepository.existsById(10L)).thenReturn(true);
        when(trainingProgramRepository.existsById(11L)).thenReturn(true);
        when(trainingProgramRepository.existsById(12L)).thenReturn(true);
        when(unitRepository.existsById(String.valueOf(1L))).thenReturn(true);
        when(unitRepository.existsById(String.valueOf(2L))).thenReturn(true);
        when(unitRepository.existsById(String.valueOf(3L))).thenReturn(true);
        when(learningObjectiveRepository.existsById(4L)).thenReturn(true);
        when(learningObjectiveRepository.existsById(5L)).thenReturn(true);
        when(learningObjectiveRepository.existsById(6L)).thenReturn(true);
        when(materialRepository.existsById(String.valueOf(7L))).thenReturn(true);
        when(materialRepository.existsById(String.valueOf(8L))).thenReturn(true);
        when(materialRepository.existsById(String.valueOf(9L))).thenReturn(true);


        // Mock the syllabusRepository.save method
        Syllabus mockSyllabus = new Syllabus();
        mockSyllabus.setId(1L); // Set a valid ID for the mock Syllabus object
        when(syllabusRepository.save(any(Syllabus.class))).thenReturn(mockSyllabus);

        // When
        ResponseEntity<?> response = syllabusService.checkSyllabusReplace(file, true, true);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Saved successfully", ((ResponseDTO) response.getBody()).getDetails().get(0));
    }


    @Test
    public void testCheckSyllabusSkip() throws IOException {
        // Given
        // Prepare a sample CSV file with test data
        String csvData = "Name,Code,Description,IsApproved,IsActive,Version,Attendee,UnitIds,LearningObjectiveIds,MaterialIds,TrainingProgramIds\n" +
                "TestName,TestCode,TestDescription,true,true,1.0,100,1/2/3,4/5/6,7/8/9,10/11/12\n" +
                "TestName2,TestCode2,TestDescription2,false,false,2.0,200,4/5/6,7/8/9,10/11/12,13/14/15";
        MultipartFile file = new MockMultipartFile("test.csv", csvData.getBytes());

        // Mock the repository and service dependencies
        when(syllabusRepository.getAllSyllabusByName(anyString())).thenReturn(Collections.emptyList());
        when(syllabusRepository.getAllSyllabusByCode(anyString())).thenReturn(Collections.emptyList());
        when(syllabusRepository.getAllSyllabusByNameAndCode(anyString(), anyString())).thenReturn(Collections.emptyList());
//        when(syllabusRepository.save(any(SyllabusDTO.class))).thenReturn(new Syllabus());

        // When
        ResponseEntity<?> response = syllabusService.checkSyllabusSkip(file, true, true);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Saved successfully", ((ResponseDTO) response.getBody()).getDetails().get(0));
    }

//    @Test
//    public void testGetCellValueAsString() {
//        // Given
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet();
//        XSSFRow row = sheet.createRow(0);
//        XSSFCell stringCell = row.createCell(0);
//        stringCell.setCellValue("TestString");
//        XSSFCell numericCell = row.createCell(1);
//        numericCell.setCellValue(123);
//        XSSFCell booleanCell = row.createCell(2);
//        booleanCell.setCellValue(true);
//        XSSFCell emptyCell = row.createCell(3);
//
//        // When
//        String stringResult = syllabusService.getCellValueAsString(stringCell);
//        String numericResult = syllabusService.getCellValueAsString(numericCell);
//        String booleanResult = syllabusService.getCellValueAsString(booleanCell);
//        String emptyResult = syllabusService.getCellValueAsString(emptyCell);
//
//        // Then
//        assertEquals("TestString", stringResult);
//        assertEquals("123", numericResult);
//        assertEquals("true", booleanResult);
//        assertEquals("", emptyResult);
//    }

    @Test
    public void testChangeStatusforUpload() {
        // Given
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L, 2L)); // Assuming there are two valid IDs
        Boolean name = true;
        Boolean code = false;
        Syllabus syllabus1 = new Syllabus();
        syllabus1.setId(1L);
        syllabus1.setName("Test1");
        Syllabus syllabus2 = new Syllabus();
        syllabus2.setId(2L);
        syllabus2.setName("Test2");
        List<Syllabus> syllabusList = Arrays.asList(syllabus1, syllabus2);
        when(syllabusRepository.findOneById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return syllabusList.stream()
                    .filter(syllabus -> syllabus.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        });

        // When
        ResponseEntity<?> response = syllabusService.changeStatusforUpload(ids, name, code);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Delete Susscessfully", ((ResponseDTO) response.getBody()).getDetails().get(0));
    }

    @Test
    public void testChangeStatusforUpload1() {
        // Given
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L, 2L)); // Assuming there are two valid IDs
        Boolean name = true;
        Boolean code = false;

        Syllabus syllabus1 = new Syllabus();
        syllabus1.setId(1L);
        syllabus1.setName("Test1");
        syllabus1.setStatus(true);

        Syllabus syllabus2 = new Syllabus();
        syllabus2.setId(2L);
        syllabus2.setName("Test2");
        syllabus2.setStatus(true);

        when(syllabusRepository.findOneById(1L)).thenReturn(syllabus1);
        when(syllabusRepository.findOneById(2L)).thenReturn(syllabus2);

        // When
        ResponseEntity<?> response = syllabusService.changeStatusforUpload(ids, name, code);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Delete Susscessfully", ((ResponseDTO) response.getBody()).getDetails().get(0));

        // Verify that the status of the syllabuses has been changed
        assertFalse(syllabus1.getStatus());
        assertFalse(syllabus2.getStatus());

        // Verify that the syllabuses have been saved with the new status
        verify(syllabusRepository, times(1)).save(syllabus1);
        verify(syllabusRepository, times(1)).save(syllabus2);
    }

    @Test
    public void testChangeStatusforUpload_EntityNotFound() {
        // Given
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L)); // Assuming there is one invalid ID
        Boolean name = true;
        Boolean code = false;

        when(syllabusRepository.findOneById(anyLong())).thenReturn(null);

        // When
        ResponseEntity<?> response = syllabusService.changeStatusforUpload(ids, name, code);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testChangeStatusforUpload_NameTrue_CodeFalse() {
        // Given
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L)); // Assuming there is one valid ID
        Boolean name = true;
        Boolean code = false;

        Syllabus syllabus = new Syllabus();
        syllabus.setId(1L);
        syllabus.setName("Test");
        syllabus.setStatus(true);

        when(syllabusRepository.findOneById(anyLong())).thenReturn(syllabus);
        when(syllabusRepository.getAllSyllabusByName(anyString())).thenReturn(Arrays.asList(syllabus, new Syllabus()));

        // When
        ResponseEntity<?> response = syllabusService.changeStatusforUpload(ids, name, code);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testChangeStatusforUpload_NameFalse_CodeTrue() {
        // Given
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L)); // Assuming there is one valid ID
        Boolean name = false;
        Boolean code = true;

        Syllabus syllabus = new Syllabus();
        syllabus.setId(1L);
        syllabus.setCode("Test");
        syllabus.setStatus(true);

        when(syllabusRepository.findOneById(anyLong())).thenReturn(syllabus);
        when(syllabusRepository.getAllSyllabusByCode(anyString())).thenReturn(Arrays.asList(syllabus, new Syllabus()));

        // When
        ResponseEntity<?> response = syllabusService.changeStatusforUpload(ids, name, code);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testChangeStatusforUpload_NameTrue_CodeTrue() {
        // Given
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L)); // Assuming there is one valid ID
        Boolean name = true;
        Boolean code = true;

        Syllabus syllabus = new Syllabus();
        syllabus.setId(1L);
        syllabus.setName("Test");
        syllabus.setCode("Test");
        syllabus.setStatus(true);

        when(syllabusRepository.findOneById(anyLong())).thenReturn(syllabus);
        when(syllabusRepository.getAllSyllabusByNameAndCode(anyString(), anyString())).thenReturn(Arrays.asList(syllabus, new Syllabus()));

        // When
        ResponseEntity<?> response = syllabusService.changeStatusforUpload(ids, name, code);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testChangeStatusforUpload_EntityNotFound1() {
        // Given
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L)); // Assuming there is one invalid ID
        Boolean name = true;
        Boolean code = false;

        when(syllabusRepository.findOneById(anyLong())).thenReturn(null);

        // When
        ResponseEntity<?> response = syllabusService.changeStatusforUpload(ids, name, code);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testChangeStatusforUpload_NameTrue_CodeFalse1() {
        // Given
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L)); // Assuming there is one valid ID
        Boolean name = true;
        Boolean code = false;

        Syllabus syllabus = new Syllabus();
        syllabus.setId(1L);
        syllabus.setName("Test");
        syllabus.setStatus(true);

        when(syllabusRepository.findOneById(anyLong())).thenReturn(syllabus);
        when(syllabusRepository.getAllSyllabusByName(anyString())).thenReturn(Arrays.asList(syllabus, new Syllabus()));

        // When
        ResponseEntity<?> response = syllabusService.changeStatusforUpload(ids, name, code);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testChangeStatusforUpload_NameFalse_CodeTrue1() {
        // Given
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L)); // Assuming there is one valid ID
        Boolean name = false;
        Boolean code = true;

        Syllabus syllabus = new Syllabus();
        syllabus.setId(1L);
        syllabus.setCode("Test");
        syllabus.setStatus(true);

        when(syllabusRepository.findOneById(anyLong())).thenReturn(syllabus);
        when(syllabusRepository.getAllSyllabusByCode(anyString())).thenReturn(Arrays.asList(syllabus, new Syllabus()));

        // When
        ResponseEntity<?> response = syllabusService.changeStatusforUpload(ids, name, code);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testChangeStatusforUpload_NameTrue_CodeTrue1() {
        // Given
        DeleteReplace ids = new DeleteReplace();
        ids.setId(Arrays.asList(1L)); // Assuming there is one valid ID
        Boolean name = true;
        Boolean code = true;

        Syllabus syllabus = new Syllabus();
        syllabus.setId(1L);
        syllabus.setName("Test");
        syllabus.setCode("Test");
        syllabus.setStatus(true);

        when(syllabusRepository.findOneById(anyLong())).thenReturn(syllabus);
        when(syllabusRepository.getAllSyllabusByNameAndCode(anyString(), anyString())).thenReturn(Arrays.asList(syllabus, new Syllabus()));

        // When
        ResponseEntity<?> response = syllabusService.changeStatusforUpload(ids, name, code);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testCheckSyllabusReplace_NameTrue_CodeFalse() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile("test.csv", "test data".getBytes());
        Boolean name = true;
        Boolean code = false;

        SyllabusDTO syllabusDTO = new SyllabusDTO();
        syllabusDTO.setUnitIds(Collections.emptyList()); // Set any required fields

        List<SyllabusDTO> syllabusDTOs = Collections.singletonList(syllabusDTO);
        when(syllabusService.checkCsvFile(file)).thenReturn(ResponseEntity.ok().build());
        when(syllabusService.parseCsvFile(file)).thenReturn(syllabusDTOs);
        when(syllabusRepository.getAllSyllabusByName(anyString())).thenReturn(Collections.emptyList());

        // Mock the save method to return a successful response
        when(syllabusService.save(any(SyllabusDTO.class))).thenReturn(ResponseEntity.ok().build());

        // When
        ResponseEntity<?> response = syllabusService.checkSyllabusReplace(file, name, code);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void testCheckSyllabusReplace_NameFalse_CodeTrue() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile("test.csv", "test data".getBytes());
        Boolean name = false;
        Boolean code = true;

        List<SyllabusDTO> syllabusDTOs = Collections.singletonList(new SyllabusDTO());
        when(syllabusServiceMockBean.parseCsvFile(file)).thenReturn(syllabusDTOs);
        when(syllabusRepositoryMockBean.getAllSyllabusByCode(anyString())).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<?> response = syllabusService.checkSyllabusReplace(file, name, code);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testCheckSyllabusReplace_NameTrue_CodeTrue() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile("test.csv", "test data".getBytes());
        Boolean name = true;
        Boolean code = true;

        List<SyllabusDTO> syllabusDTOs = Collections.singletonList(new SyllabusDTO());
        when(syllabusServiceMockBean.parseCsvFile(file)).thenReturn(syllabusDTOs);
        when(syllabusRepositoryMockBean.getAllSyllabusByNameAndCode(anyString(), anyString())).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<?> response = syllabusService.checkSyllabusReplace(file, name, code);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testCheckSyllabusReplace_BadRequest() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile("test.csv", "test data".getBytes());
        Boolean name = true;
        Boolean code = true;

        // Mock parseCsvFile to throw an exception or return null
        when(syllabusServiceMockBean.parseCsvFile(file)).thenThrow(new IOException("Error parsing file"));

        // When
        ResponseEntity<?> response = syllabusService.checkSyllabusReplace(file, name, code);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
