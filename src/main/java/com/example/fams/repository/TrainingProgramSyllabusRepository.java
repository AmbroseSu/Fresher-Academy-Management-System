package com.example.fams.repository;

import com.example.fams.entities.Content;
import com.example.fams.entities.Syllabus;
import com.example.fams.entities.TrainingProgramSyllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TrainingProgramSyllabusRepository extends JpaRepository<TrainingProgramSyllabus, Long> {
    @Modifying
    @Transactional
    Integer deleteAllByTrainingProgramId(Long trainingProgramId);

    @Query("SELECT s FROM Syllabus s " +
            "JOIN TrainingProgramSyllabus loc ON s.id = loc.syllabus.id " +
            "WHERE loc.trainingProgram.id = :trainingProgramId")
    List<Syllabus> findSyllabusByTrainingProgramId(Long trainingProgramId);
}
