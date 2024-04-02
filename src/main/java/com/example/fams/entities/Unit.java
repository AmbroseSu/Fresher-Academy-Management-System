package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="tbl_unit")
public class Unit extends BaseEntity{

    @ManyToOne
    @JoinColumn(name="syllabusId")
    private Syllabus syllabus;

    private String name;

//    private Integer duration;

    @OneToMany(mappedBy="unit")
    private List<Content> contents;
}