package com.example.fams.repository;

import com.example.fams.entities.LearningObjectiveContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface LearningObjectiveContentRepository extends JpaRepository<LearningObjectiveContent, Long> {
    @Modifying
    @Transactional
    Integer deleteAllByLearningObjectiveId(Long learningObjectiveId);
}
