package com.example.fams.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputStandardDTO {
    private Long id;

    @NotNull(message = "Output standard name must not be null")
    private String outputStandardName;

    private Long syllabusId;
}
