package com.example.fams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitDTO {

    private Long id;

    private Long syllabusId;

    @NotBlank(message = "Unit Name must not be blank")
    private String name;

    // @NotNull(message = "Duration must not be null")
    private Integer duration;

    private Boolean status;

    @Pattern(regexp = "^\\d+$", message = "Day number must contain only numeric values")
    private String dayNumber;

    private List<Long> contentIds;
    private String createBy;
    private String modifiedBy;
}
