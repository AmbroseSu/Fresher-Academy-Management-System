package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="tbl_class")
public class FamsClass extends BaseEntity{
    @ManyToOne
    @JoinColumn(name="training_programID")
    private TrainingProgram trainingProgram;

    private String name;

    private String code;

    private Long duration;

    private String location;

    @Column(name="start_date")
    private Long startDate;

    @Column(name="end_date")
    private Long endDate;

//    @ManyToMany
//    private List<User> users;

    @OneToMany(mappedBy = "famsClass")
    private List<ClassUser> classUsers;

}