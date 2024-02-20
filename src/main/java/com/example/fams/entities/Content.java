package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name="tbl_content")
public class Content extends BaseEntity{

    @ManyToOne
    @JoinColumn(name="unitId")
    private Unit unit;

    @ManyToMany
    private List<LearningObjective> learningObjectives;

    private Integer deliveryType;

    private Long duration;

}