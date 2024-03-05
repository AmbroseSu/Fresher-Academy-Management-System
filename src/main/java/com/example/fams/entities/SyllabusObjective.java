package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="tbl_syllabusObjective")
public class SyllabusObjective {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "syllabusId")
    private Syllabus syllabus;

    @ManyToOne
    @JoinColumn(name = "learningObjectiveId")
    private LearningObjective learningObjective;

    @Override
    public String toString() {
        return "SyllabusObjective{" +
                "id=" + id +
                // Include only necessary fields, avoid calling toString() on collections or related entities
                '}';
    }

}
