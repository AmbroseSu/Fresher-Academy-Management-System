package com.example.fams.repository;

import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.Syllabus;
import com.example.fams.entities.SyllabusObjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SyllabusObjectiveRepository extends JpaRepository<SyllabusObjective, Long> {
    @Modifying
    @Transactional
    Integer deleteAllBySyllabusId(Long syllabusId);

    @Query("SELECT s FROM Syllabus s " +
            "JOIN SyllabusObjective loc ON s.id = loc.syllabus.id " +
            "WHERE loc.learningObjective.id = :learningObjectiveId")
    List<Syllabus> findSyllabusByLearningObjectiveId(Long learningObjectiveId);

    Integer deleteAllByLearningObjectiveId(Long learningObjectiveId);
    @Query("SELECT s FROM LearningObjective s " +
            "JOIN SyllabusObjective loc ON s.id = loc.learningObjective.id " +
            "WHERE loc.syllabus.id = :syllabusId")
    List<LearningObjective> findLearningObjectiveBySyllabusId(Long syllabusId);


    @Query("Select so from SyllabusObjective so where so.syllabus.id =: id")
    List<SyllabusObjective> findAllLearingObjectiveBySyllabusId(Long id);



}
