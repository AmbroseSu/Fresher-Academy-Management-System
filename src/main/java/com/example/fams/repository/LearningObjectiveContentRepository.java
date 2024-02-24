package com.example.fams.repository;

import com.example.fams.dto.response.LearningObjectiveResponse;
import com.example.fams.entities.Content;
import com.example.fams.entities.LearningObjectiveContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LearningObjectiveContentRepository extends JpaRepository<LearningObjectiveContent, Long> {
    @Modifying
    @Transactional
    Integer deleteAllByLearningObjectiveId(Long learningObjectiveId);

    @Query("SELECT c FROM Content c " +
            "JOIN LearningObjectiveContent loc ON c.id = loc.content.id " +
            "WHERE loc.learningObjective.id = :learningObjectiveId")
    List<Content> findContentsByLearningObjectiveId(Long learningObjectiveId);
}
