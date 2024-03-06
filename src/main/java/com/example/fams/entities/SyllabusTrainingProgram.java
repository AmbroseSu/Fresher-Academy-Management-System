package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="tbl_syllabusTrainingProgram")
public class SyllabusTrainingProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "syllabusId")
    private Syllabus syllabus;

    @ManyToOne
    @JoinColumn(name = "trainingProgramId")
    private TrainingProgram trainingProgram;

    @Override
    public String toString() {
        return "SyllabusTrainingProgram{" +
                "id=" + id +
                // Include only necessary fields, avoid calling toString() on collections or related entities
                '}';
    }
}
