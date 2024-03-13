package com.example.fams.dto;

import com.example.fams.entities.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String uuid;
    private String phone;
    private Long dob;
    private Boolean status;
    private Boolean gender;
    private String avatarUrl;
    private MultipartFile avatar;
    private List<Long> classIds;
    @NotNull(message = "User Role Id must not be null")
    private Long userRoleId;
}
