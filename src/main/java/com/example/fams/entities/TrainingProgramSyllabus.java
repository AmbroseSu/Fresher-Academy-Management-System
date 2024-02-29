package com.example.fams.entities;

import com.example.fams.entities.Syllabus;
import com.example.fams.entities.TrainingProgram;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="tbl_TrainingProgramSyllabus")
public class TrainingProgramSyllabus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainingProgramId")
    private TrainingProgram trainingProgram;

    @ManyToOne
    @JoinColumn(name = "syllabusId")
    private Syllabus syllabus;

}