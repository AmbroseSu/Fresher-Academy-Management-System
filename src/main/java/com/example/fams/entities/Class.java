package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Class {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name="training_programID")
    private TrainingProgram trainingProgram;

    private String name;

    private String code;

    private Long duration;

    private Integer status;

    private String location;

    @Column(name="start_date")
    private Long startDate;

    @Column(name="end_date")
    private Long endDate;

    private String createBy;

    @Column(name="create_date")
    private Long createDate;

    @Column(name="modified_by")
    private String modifiedBy;

    @Column(name="modified_date")
    private Long modifiedDate;

    @ManyToMany
    private List<User> users;

}