package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="tbl_trainingProgram")
public class TrainingProgram {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Long startTime;

    private Long duration;

    private Integer status;

    private String createBy;

    private Long createDate;

    private String modifiedBy;

    private Long modifiedDate;

    @ManyToMany
    private List<Syllabus> syllabuses;

}