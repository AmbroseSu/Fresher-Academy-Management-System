package com.example.fams.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDTO {

  private Long id;
  @NotNull(message = "Please enter your name")
  private String name;
  @NotNull(message = "Please enter code")
  private String code;
//  private Long duration;
  private Boolean status;
//  private Long startDate;
//  private Long endDate;
  private List<Long> userIds;
}
