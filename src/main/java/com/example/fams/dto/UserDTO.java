package com.example.fams.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
  private Integer id;
  private String firstName;
  private String secondName;
  private String email;
  private String phone;
  private Boolean gender;
}
