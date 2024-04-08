package com.example.fams.entities;

import com.example.fams.entities.enums.ClassStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@Table(name = "tbl_class")
public class FamsClass extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "training_programID")
    private TrainingProgram trainingProgram;
    private String fsu;
    private String name;

    private String code;

    private Long duration;

    private ClassStatus classStatus;

    private String location;

    @Column(name = "start_date")
    private Long startDate;

    @Column(name = "end_date")
    private Long endDate;

    @OneToMany(mappedBy = "famsClass")
    private List<ClassUser> classUsers;

    @OneToMany (mappedBy = "famsClass")
    private List<CalendarClass> calendarsByWeekDay;

    private LocalTime startTime;

    private LocalTime endTime;
}