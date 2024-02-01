package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name="tbl_content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="unitId")
    private Unit unit;

    @ManyToMany
    private List<LearningObjective> learningObjectives;

    private Integer deliveryType;

    private Long duration;

    private String createBy;

    private Long createDate;

    private String modifiedBy;

    private Long modifiedDate;

}