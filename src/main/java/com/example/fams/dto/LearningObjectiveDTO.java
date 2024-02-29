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

    @NotBlank(message = "Code must not be blank")
    private String code;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Type must not be null")
    private Integer type;

    @NotBlank(message = "Description must not be blank")
    private String description;

    private Boolean status;

    private List<ContentDTO> contentDTOs;
}