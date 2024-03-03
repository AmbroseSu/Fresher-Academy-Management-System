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
public class UnitDTO {

    private Long id;

    private SyllabusDTO syllabusDTO;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Duration must not be null")
    private Integer duration;

    private Boolean status;

    private List<ContentDTO> contentDTOs;
}
