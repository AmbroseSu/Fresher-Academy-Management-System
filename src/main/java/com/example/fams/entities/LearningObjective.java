package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Data
@Entity
@Table(name="tbl_learningObjective")
public class LearningObjective extends BaseEntity {

    private String code;

    private String name;

    private Integer type;

    private String description;

    @OneToMany(mappedBy="learningObjective")
    private List<LearningObjectiveContent> learningObjectiveContents;

    @OneToMany(mappedBy = "learningObjective")
    private List<SyllabusObjective> syllabusObjectives;

    @Override
    public String toString() {
        return "LearningObjective{" +
                "id=" + super.getId() +
                // Include only necessary fields, avoid calling toString() on collections or related entities
                '}';
    }

}