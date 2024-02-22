package com.example.fams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusDTO {
    private Long id;

    private String name;

    private String code;

    private Long timeAllocation;

    private String description;

    private Boolean isApproved;

    private Boolean isActive;

    private String version;

    private boolean status;

}
