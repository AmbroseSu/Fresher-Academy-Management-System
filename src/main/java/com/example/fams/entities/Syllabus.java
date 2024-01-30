package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
@Table(name="tbl_syllabus")
public class Syllabus {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String code;

    private Long timeAllocation;

    private String description;

    private Boolean isApproved;

    private Boolean isActive;

    private String createBy;

    private Long createDate;

    private String version;

    @ManyToMany
    private List<Material> materials;

    @ManyToMany
    private List<LearningObjective> learningObjectives;

    @ManyToMany(mappedBy="syllabuses")
    private List<TrainingProgram> trainingPrograms;

}