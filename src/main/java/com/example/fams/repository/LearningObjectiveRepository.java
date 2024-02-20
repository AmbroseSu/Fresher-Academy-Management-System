package com.example.fams.repository;

import com.example.fams.entities.LearningObjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LearningObjectiveRepository extends JpaRepository<LearningObjective, String> {
    List<LearningObjective> findByStatusIsTrue();
    LearningObjective findById(Long id);
    LearningObjective findByStatusIsTrueAndCode(String code);
    LearningObjective findByStatusIsTrueAndId(Long id);
    @Transactional
    @Modifying
    @Query("UPDATE LearningObjective lo SET lo.status = ?1 WHERE lo.code = ?2")
    LearningObjective changeStatus(Boolean status, String code);
}
