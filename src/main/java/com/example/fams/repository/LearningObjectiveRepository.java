package com.example.fams.repository;

import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.Material;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LearningObjectiveRepository extends JpaRepository<LearningObjective, String> {
    List<LearningObjective> findAllByStatusIsTrue(Pageable pageable);
    List<LearningObjective> findAllByOrderByIdDesc(Pageable pageable);
    LearningObjective findById(Long id);
    LearningObjective findByStatusIsTrueAndId(Long id);
    LearningObjective findByStatusIsTrueAndCode(String code);
    Long countAllByStatusIsTrue();
    @Transactional
    @Modifying
    @Query("UPDATE LearningObjective lo SET lo.status = ?1 WHERE lo.code = ?2")
    LearningObjective changeStatus(Boolean status, String code);
}
