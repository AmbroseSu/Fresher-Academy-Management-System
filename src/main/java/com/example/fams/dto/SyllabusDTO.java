package com.example.fams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusDTO {
    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Code must not be blank")
    private String code;

    @NotNull(message = "Time Allocation must not be null")
    private Long timeAllocation;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Is Approved must not be null")
    private Boolean isApproved;

    @NotNull(message = "Is Active must not be null")
    private Boolean isActive;

    @NotBlank(message = "Version must not be blank")
    private String version;

    private Boolean status;

    private List<Long> unitIds;
    private List<Long> learningObjectiveIds;
    private List<Long> materialIds;
    private List<Long> trainingProgramIds;
}