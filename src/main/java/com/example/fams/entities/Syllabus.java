package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
@Table(name="tbl_syllabus")
public class Syllabus extends BaseEntity{

    private Long attendee;
    private String name;

    private String code;

    private Long timeAllocation;

    private String description;

    private Boolean isApproved;

    private Boolean isActive;

    private String version;

    @OneToMany(mappedBy="syllabus")
    private List<SyllabusMaterial> syllabusMaterial;

    @OneToMany(mappedBy = "syllabus")
    private List<SyllabusObjective> syllabusObjectives;

    @OneToMany(mappedBy = "syllabus")
    private List<SyllabusTrainingProgram> syllabusTrainingPrograms;

    @OneToMany(mappedBy = "syllabus")
    private List<Unit> units;

    @OneToMany(mappedBy = "syllabus")
    private List<OutputStandard> outputStandards;
}