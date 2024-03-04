package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="tbl_material")
public class Material extends BaseEntity{

    private String name;

    private String description;

    @OneToMany(mappedBy="material")
    private List<SyllabusMaterial> syllabusMaterial;



}