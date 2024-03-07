package com.example.fams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LearningObjectiveDTO {
    private Long id;

    @NotBlank(message = "Learning Objective Code must not be blank")
    private String code;

    @NotBlank(message = "Learning Objective Name must not be blank")
    private String name;

    @NotNull(message = "Learning Objective Type must not be null")
    private Integer type;

    @NotBlank(message = "Learning Objective Description must not be blank")
    private String description;

    private Boolean status;

    private List<Long> contentIds;
    private List<Long> syllabusIds;
}