package com.example.fams.repository;

import com.example.fams.dto.response.LearningObjectiveResponse;
import com.example.fams.entities.Content;
import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.LearningObjectiveContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LearningObjectiveContentRepository extends JpaRepository<LearningObjectiveContent, String> {

    LearningObjectiveContent findById(Long id);
    @Modifying
    @Transactional
    Integer deleteAllByLearningObjectiveId(Long learningObjectiveId);

    @Query("SELECT loc FROM LearningObjectiveContent loc WHERE loc.learningObjective.id = :learningObjectiveId AND loc.content.id = :contentId")
    LearningObjectiveContent findLearningObjectiveContentByLearningObjectiveIdAndContentId(Long learningObjectiveId, Long contentId);

    @Query("SELECT c FROM Content c " +
            "JOIN LearningObjectiveContent loc ON c.id = loc.content.id " +
            "WHERE loc.learningObjective.id = :learningObjectiveId")
    List<Content> findContentsByLearningObjectiveId(Long learningObjectiveId);

    @Query("SELECT lo FROM LearningObjective lo " +
            "JOIN LearningObjectiveContent loc ON lo.id = loc.learningObjective.id " +
            "WHERE loc.content.id = :contentId")
    List<LearningObjective> findLearningObjectivesByContentId(Long contentId);
}
