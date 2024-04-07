package com.example.fams.entities;

import com.example.fams.entities.enums.DeliveryType;
import com.example.fams.entities.enums.TrainingFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name="tbl_content")
public class Content extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="unitId")
    private Unit unit;

    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

    @Enumerated(EnumType.STRING)
    private TrainingFormat trainingFormat;

    private Long duration;

    @OneToMany(mappedBy="content")
    private List<LearningObjectiveContent> learningObjectiveContents;

    @OneToMany(mappedBy = "content")
    private List<OutputStandard> outputStandards;

    @Override
    public String toString() {
        return "Content{" +
            "id=" + super.getId() +
            // Include only necessary fields, avoid calling toString() on collections or related entities
            '}';
    }
}