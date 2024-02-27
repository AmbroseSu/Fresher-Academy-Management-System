package com.example.fams.repository;

import com.example.fams.entities.Content;
import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.LearningObjectiveContent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ContentLearningObjectiveRepository extends JpaRepository<LearningObjectiveContent, Long> {
  @Modifying
  @Transactional
  Integer deleteAllByContentId(Long contentId);

  @Query("SELECT lo FROM LearningObjective lo " +
      "JOIN LearningObjectiveContent loc ON lo.id = loc.learningObjective.id " +
      "WHERE loc.content.id = :contentId")
  List<LearningObjective> findLearningObjectivesByContentId(Long contentId);
}
