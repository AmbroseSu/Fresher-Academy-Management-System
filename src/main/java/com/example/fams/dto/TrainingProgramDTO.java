package com.example.fams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingProgramDTO {
    private Long id;

    private String name;

    private Long startTime;

    private Long duration;

    private Integer training_status;

    private boolean status;
}
