package com.example.fams.dto;

import com.example.fams.entities.Syllabus;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingProgramDTO {
    private Long id;

    private String name;

    private Long startTime;

    private Long duration;

    private Integer training_status;

    private Boolean status;

    private List<SyllabusDTO> syllabusDTOs;
}