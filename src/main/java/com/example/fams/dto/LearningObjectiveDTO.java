package com.example.fams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LearningObjectiveDTO {
    private Long id;

    private String code;

    private String name;

    private Integer type;

    private String description;

    private Boolean status;

}
