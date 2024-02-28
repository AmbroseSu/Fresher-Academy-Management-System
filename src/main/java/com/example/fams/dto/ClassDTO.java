package com.example.fams.dto;

import java.util.List;
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
  private Boolean status;
  private Long startDate;
  private Long endDate;
  private List<UserDTO> userDTOs;
}
