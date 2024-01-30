package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Material {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @ManyToMany(mappedBy="materials")
    private List<Syllabus> syllabuses;

}