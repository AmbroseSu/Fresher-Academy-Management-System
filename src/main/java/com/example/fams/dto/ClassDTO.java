package com.example.fams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDTO {

  private Long id;
  private String name;
  private String code;
  private Long duration;
  private Integer status;
  private Long startDate;
  private Long endDate;
}
