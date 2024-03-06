package com.example.fams.repository;

import com.example.fams.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SyllabusTrainingProgramRepository extends JpaRepository<SyllabusTrainingProgram, Long> {
    @Modifying
    @Transactional
    Integer deleteAllBySyllabusId(Long syllabusId);
    @Query("SELECT t FROM TrainingProgram t " +
            "JOIN SyllabusTrainingProgram loc ON t.id = loc.trainingProgram.id " +
            "WHERE loc.syllabus.id = :syllabusId")
    List<TrainingProgram> findTrainingProgramBySyllabusId(Long syllabusId);
    @Modifying
    @Transactional
    Integer deleteAllByTrainingProgramId(Long trainingProgramId);

    @Query("SELECT s FROM Syllabus s " +
            "JOIN SyllabusTrainingProgram loc ON s.id = loc.syllabus.id " +
            "WHERE loc.trainingProgram.id = :trainingProgramId")
    List<Syllabus> findSyllabusByTrainingProgramId(Long trainingProgramId);

//    @Modifying
//    @Transactional
//    @Query("DELETE FROM SyllabusMaterial sm WHERE sm.syllabus.id = :syllabusId")
//    void deleteAllMaterialBySyllabusId(Long syllabusId);

    @Query("SELECT st FROM SyllabusTrainingProgram st WHERE st.syllabus.id = :syllabusId")
    List<SyllabusTrainingProgram> findAllTrainingProgramSyllabusBySyllabusId(Long syllabusId);


}
