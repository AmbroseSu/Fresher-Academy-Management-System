package com.example.fams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    private List<ContentDTO> contentDTOs;
}
