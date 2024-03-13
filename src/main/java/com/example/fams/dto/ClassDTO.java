package com.example.fams.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDTO {

  private Long id;
  @NotBlank(message = "FSU must not be blank")
  private String fsu;
  @NotBlank(message = "Class Name must not be blank")
  private String name;
  @NotBlank(message = "Please enter class code")
  private String code;
//  private Long duration;
  private Boolean status;
//  private Long startDate;
//  private Long endDate;
  private List<Long> userIds;
  private Long trainingProgramId;
}
