package com.example.fams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitDTO {

    private Long id;

    private String name;

    private Integer duration;

    private Boolean status;
}
