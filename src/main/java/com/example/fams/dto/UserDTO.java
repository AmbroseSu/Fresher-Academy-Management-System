package com.example.fams.dto;

import java.util.List;

import com.example.fams.entities.enums.Role;
import jakarta.validation.constraints.NotBlank;
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

  @NotBlank(message = "Email must not be blank")
  private String email;

  @NotBlank(message = "Phone must not be blank")
  private String phone;

  private Boolean gender;

  private Role role;
}
