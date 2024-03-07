package com.example.fams.repository;

import com.example.fams.entities.*;
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

    @Query("SELECT sl FROM Syllabus sl " +
            "WHERE (:name IS NULL OR sl.name = :name) AND sl.status = TRUE " +
            "AND (:code IS NULL OR sl.code = :code) " +
            "AND (:timeAllocation IS NULL OR sl.timeAllocation = :timeAllocation) " +
            "AND (:description IS NULL OR sl.description = :description) " +
            "AND (:isApproved IS NULL OR sl.isApproved = :isApproved) " +
            "AND (:isActive IS NULL OR sl.isActive = :isActive) " +
            "AND (:version IS NULL OR sl.version = :version) " )
    List<Syllabus> searchSortFilter(@Param("name") String name,
                                    @Param("code") String code,
                                    @Param("timeAllocation") Long timeAllocation,
                                    @Param("description") String description,
                                    @Param("isApproved") Boolean isApproved,
                                    @Param("isActive") Boolean isActive,
                                    @Param("version") String version,
                                    Pageable pageable);

    @Query("SELECT COUNT(sl) FROM Syllabus sl " +
            "WHERE (:name IS NULL OR sl.name = :name) AND sl.status = TRUE " +
            "AND (:code IS NULL OR sl.code = :code) " +
            "AND (:timeAllocation IS NULL OR sl.timeAllocation = :timeAllocation) " +
            "AND (:description IS NULL OR sl.description = :description) " +
            "AND (:isApproved IS NULL OR sl.isApproved = :isApproved) " +
            "AND (:isActive IS NULL OR sl.isActive = :isActive) " +
            "AND (:version IS NULL OR sl.version = :version) " )
    Long countSearchSortFilter(@Param("name") String name,
                               @Param("code") String code,
                               @Param("timeAllocation") Long timeAllocation,
                               @Param("description") String description,
                               @Param("isApproved") Boolean isApproved,
                               @Param("isActive") Boolean isActive,
                               @Param("version") String version);



    @Query("SELECT sl FROM Syllabus sl " +
            "WHERE (:name IS NULL OR sl.name = :name) AND sl.status = TRUE " +
            "AND (:code IS NULL OR sl.code = :code) " +
            "AND (:timeAllocation IS NULL OR sl.timeAllocation = :timeAllocation) " +
            "AND (:description IS NULL OR sl.description = :description) " +
            "AND (:isApproved IS NULL OR sl.isApproved = :isApproved) " +
            "AND (:isActive IS NULL OR sl.isActive = :isActive) " +
            "AND (:version IS NULL OR sl.version = :version) "+
            "ORDER BY " +
            "CASE WHEN :sortById ='iDESC' THEN sl.id END DESC, " +
            "CASE WHEN :sortById ='iASC' THEN sl.id END ASC, " +
            "sl.id DESC")
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
