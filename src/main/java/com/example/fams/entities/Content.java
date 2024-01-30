package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Content {

    @Id
    private Long id;

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