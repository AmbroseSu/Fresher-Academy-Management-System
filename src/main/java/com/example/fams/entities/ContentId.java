package com.example.fams.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable
public class ContentId implements Serializable {

    private Long id;

    private Long unitId;

    private String learningObjectiveCode;

}