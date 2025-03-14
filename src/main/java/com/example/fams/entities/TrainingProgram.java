package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="tbl_trainingProgram")
public class TrainingProgram extends BaseEntity{

    private String name;

    private Long startTime;

//    private Long duration;

    private Integer training_status;

    @OneToMany(mappedBy="trainingProgram")
    private List<SyllabusTrainingProgram> syllabusTrainingPrograms;
//    @Override
//    public String toString() {
//        return "TrainingProgram{" +
//                "id=" + super.getId() +
//                // Include only necessary fields, avoid calling toString() on collections or related entities
//                '}';
//    }

}