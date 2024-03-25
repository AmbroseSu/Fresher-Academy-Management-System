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

  @NotNull(message = "Class start date must not be null")
  private Long startDate;

  @NotNull(message = "Class end date must not be null")
  private Long endDate;

  @NotNull(message = "Class start time frame must not be null")
  private Float startTimeFrame;

  @NotNull(message = "Class end time frame must not be null")
  private Float endTimeFrame;

  private List<Long> userIds;
  private Long trainingProgramId;
  private String createBy;
  private String createdDate;
  private String modifiedBy;
}
