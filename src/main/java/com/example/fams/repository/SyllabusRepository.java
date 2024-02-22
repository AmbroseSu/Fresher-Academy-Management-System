package com.example.fams.repository;

import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.Syllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SyllabusRepository extends JpaRepository<Syllabus, String> {
    List<Syllabus> findByStatusIsTrue();
    Syllabus findById(Long id);
    Syllabus findByStatusIsTrueAndCode(String code);
    Syllabus findByStatusIsTrueAndId(Long id);
    @Transactional
    @Modifying
    @Query("UPDATE Syllabus lo SET lo.status = ?1 WHERE lo.code = ?2")
    Syllabus changeStatus(Boolean status, String code);

}
