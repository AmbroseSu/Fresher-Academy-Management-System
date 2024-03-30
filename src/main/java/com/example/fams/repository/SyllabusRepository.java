package com.example.fams.repository;

import com.example.fams.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SyllabusRepository extends JpaRepository<Syllabus, Long > {

    List<Syllabus> findAllByStatusIsTrue(Pageable pageable);
    List<Syllabus> findAllByOrderByIdDesc(Pageable pageable);
    Syllabus findOneById(Long id);
    Syllabus findByStatusIsTrueAndId(Long id);
    Syllabus findByStatusIsTrueAndName(String name);
    Long countAllByStatusIsTrue();
    @Transactional
    @Modifying
    @Query("UPDATE Syllabus sl SET sl.status = ?1 WHERE sl.id = ?2")
    void changeStatus(Boolean status, Long id);

    @Query("SELECT S FROM Syllabus S WHERE S.name = :name AND S.status = TRUE ")
    List<Syllabus> getAllSyllabusByName(@Param("name") String name);

    @Query("SELECT S FROM Syllabus S WHERE S.code = :code AND S.status = TRUE ")
    List<Syllabus> getAllSyllabusByCode(@Param("code") String code);

    @Query("SELECT S FROM Syllabus S WHERE S.name = :name AND S.code = :code AND S.status = TRUE ")
    List<Syllabus> getAllSyllabusByNameAndCode(@Param("name") String name, @Param("code") String code);

    @Query("SELECT tp FROM TrainingProgram tp " +
            "JOIN SyllabusTrainingProgram stp ON tp.id = stp.trainingProgram.id " +
            "WHERE stp.syllabus.id = :syllabusId")
    List<TrainingProgram> findTrainingProgramsBySyllabusId(Long syllabusId);
    @Query("SELECT m FROM Material m " +
            "JOIN SyllabusMaterial sm ON m.id = sm.material.id " +
            "WHERE sm.syllabus.id = :syllabusId")
    List<Material> findMaterialsBySyllabusId(Long syllabusId);
    @Query("SELECT lo FROM LearningObjective lo " +
            "JOIN SyllabusObjective so ON lo.id = so.learningObjective.id " +
            "WHERE so.syllabus.id = :syllabusId")
    List<LearningObjective> findLearningObjectivesBySyllabusId(Long syllabusId);
    @Query("SELECT u FROM Unit u WHERE u.syllabus.id = :syllabusId")
    List<Unit> findUnitsBySyllabusId(Long syllabusId);

    @Query("SELECT os FROM OutputStandard os WHERE os.syllabus.id = :syllabusId")
    List<OutputStandard> findOutputStandardsBySyllabusId(Long syllabusId);

    @Query(value = "SELECT * FROM tbl_syllabus sl " +
            "WHERE (:name IS NULL OR LOWER(sl.name) LIKE LOWER(CONCAT('%', :name,'%'))) AND sl.status = TRUE " +
            "AND (:code IS NULL OR LOWER(sl.code) LIKE LOWER(CONCAT('%', :code,'%'))) " +
            "AND (:timeAllocation IS NULL OR sl.time_allocation = :timeAllocation) " +
            "AND (:description IS NULL OR sl.description = :description) " +
            "AND (:isApproved IS NULL OR sl.is_approved = :isApproved) " +
            "AND (:isActive IS NULL OR sl.is_active = :isActive) " +
            "AND (:version IS NULL OR sl.version = :version) " +
            "ORDER BY " +
            "CASE WHEN :sort_by_created_date ='cDESC' THEN sl.created_date END DESC, " +
            "CASE WHEN :sort_by_created_date ='cASC' THEN sl.created_date END ASC, " +
            "sl.created_date DESC", nativeQuery = true)
    List<Syllabus> searchSortFilter(@Param("name") String name,
                                    @Param("code") String code,
                                    @Param("timeAllocation") Long timeAllocation,
                                    @Param("description") String description,
                                    @Param("isApproved") Boolean isApproved,
                                    @Param("isActive") Boolean isActive,
                                    @Param("version") String version,
                                    @Param("sort_by_created_date") String sortByCreatedDate,
                                    Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM tbl_syllabus sl " +
            "WHERE (:name IS NULL OR LOWER(sl.name) LIKE LOWER(CONCAT('%', :name,'%'))) AND sl.status = TRUE " +
            "AND (:code IS NULL OR LOWER(sl.code) LIKE LOWER(CONCAT('%', :code,'%'))) " +
            "AND (:timeAllocation IS NULL OR sl.time_allocation = :timeAllocation) " +
            "AND (:description IS NULL OR sl.description = :description) " +
            "AND (:isApproved IS NULL OR sl.is_approved = :isApproved) " +
            "AND (:isActive IS NULL OR sl.is_active = :isActive) " +
            "AND (:version IS NULL OR sl.version = :version) ", nativeQuery = true )
    Long countSearchSortFilter(@Param("name") String name,
                               @Param("code") String code,
                               @Param("timeAllocation") Long timeAllocation,
                               @Param("description") String description,
                               @Param("isApproved") Boolean isApproved,
                               @Param("isActive") Boolean isActive,
                               @Param("version") String version);



    @Query(value = "SELECT * FROM tbl_syllabus sl " +
            "WHERE (:name IS NULL OR LOWER(sl.name) LIKE LOWER(CONCAT('%', :name,'%'))) " +
            "AND (:code IS NULL OR LOWER(sl.code) LIKE LOWER(CONCAT('%', :code,'%'))) " +
            "AND (:timeAllocation IS NULL OR sl.time_allocation = :timeAllocation) " +
            "AND (:description IS NULL OR sl.description = :description) " +
            "AND (:isApproved IS NULL OR sl.is_approved = :isApproved) " +
            "AND (:isActive IS NULL OR sl.is_active = :isActive) " +
            "AND (:version IS NULL OR sl.version = :version) "+
            "ORDER BY " +
            "CASE WHEN :sortById ='iDESC' THEN sl.id END DESC, " +
            "CASE WHEN :sortById ='iASC' THEN sl.id END ASC, " +
            "CASE WHEN :sortById NOT IN ('iDESC', 'iASC') THEN sl.id END DESC", nativeQuery = true)
    List<Syllabus> searchSortFilterADMIN(@Param("name") String name,
                                         @Param("code") String code,
                                         @Param("timeAllocation") Long timeAllocation,
                                         @Param("description") String description,
                                         @Param("isApproved") Boolean isApproved,
                                         @Param("isActive") Boolean isActive,
                                         @Param("version") String version,
                                         @Param("sortById") String sortById,
                                         Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Unit u SET u.syllabus.id = null WHERE u.syllabus.id = :syllabusId")
    void deleteAllUnitInSyllabusBySyllabusId(Long syllabusId);
}
