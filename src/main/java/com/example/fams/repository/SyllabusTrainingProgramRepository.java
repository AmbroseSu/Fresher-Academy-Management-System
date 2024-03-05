package com.example.fams.repository;

import com.example.fams.entities.Syllabus;
import com.example.fams.entities.SyllabusTrainingProgram;
import com.example.fams.entities.TrainingProgram;
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
}
