package com.example.fams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {

    private Long id;
    private Boolean status;
    private String name;

    private String description;
    private List<SyllabusDTO> syllabusDTOs;
}
