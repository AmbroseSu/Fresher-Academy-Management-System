package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
@Table(name="tbl_syllabus")
public class Syllabus extends BaseEntity{
    private Long id;

    private String name;

    private String code;

    private Long timeAllocation;

    private String description;

    private Boolean isApproved;

    private Boolean isActive;

    private String version;

    @OneToMany(mappedBy="syllabus")
    private List<SyllabusMaterial> syllabusMaterial;

    @ManyToMany
    private List<LearningObjective> learningObjectives;

    @OneToMany(mappedBy = "syllabus")
    private List<TrainingProgramSyllabus> trainingProgramSyllabuses;


}