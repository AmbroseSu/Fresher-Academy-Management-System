package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="tbl_syllabusMaterial")
public class SyllabusMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "syllabusId")
    private Syllabus syllabus;

    @ManyToOne
    @JoinColumn(name = "materialId")
    private Material material;
}


