package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="tbl_unit")
public class Unit {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name="syllabusId")
    private Syllabus syllabus;

    private String name;

    private Integer duration;

    private String createBy;

    private Long createDate;

    private String modifiedBy;

    private Long modifiedDate;

}