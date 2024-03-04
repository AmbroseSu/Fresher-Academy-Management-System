package com.example.fams.dto;

import com.example.fams.entities.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpsertUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private String uuid;
    private String phone;
    private Long dob;
    private Boolean gender;
    private List<ClassDTO> classes;
    private Long createdDate;
    private String createBy;
    private String modifiedBy;
    private Long modifiedDate;
}
