package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="tbl_learningObjectiveContent")
public class LearningObjectiveContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "learningObjectiveId")
    private LearningObjective learningObjective;

    @ManyToOne
    @JoinColumn(name = "contentId")
    private Content content;
}
