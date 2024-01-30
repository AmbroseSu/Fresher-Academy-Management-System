package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class LearningObjective {

    @Id
    private String code;

    private String name;

    private Integer type;

    private String description;

    private String createBy;

    private Long createDate;

    private String modifiedBy;

    private Long modifiedDate;

    @ManyToMany(mappedBy="learningObjectives")
    private List<Content> contents;

}