package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@Entity
@Table(name="tbl_class")
public class FamsClass extends BaseEntity{
    @ManyToOne
    @JoinColumn(name="training_programID")
    private TrainingProgram trainingProgram;
    private String fsu;
    private String name;

    private String code;

    private Long duration;

    private String location;

    @Column(name="start_date")
    private Long startDate;

    @Column(name="end_date")
    private Long endDate;

    @Column(name="start_time_frame")
    private Float startTimeFrame;

    @Column(name="end_time_frame")
    private Float endTimeFrame;

    @OneToMany(mappedBy = "famsClass")
    private List<ClassUser> classUsers;

}