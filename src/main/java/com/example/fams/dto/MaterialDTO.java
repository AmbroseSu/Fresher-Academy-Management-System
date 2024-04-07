package com.example.fams.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {

    private Long id;
    @NotBlank(message = "Material Name must not be blank")
    private String name;
    @NotBlank(message = "Description must not be blank")
    private String description;
    private String url;
    private Boolean status;
    private List<Long> syllabusIds;
    private String createBy;
    private String modifiedBy;
}
