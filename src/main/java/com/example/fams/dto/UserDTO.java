package com.example.fams.dto;

import com.example.fams.controller.AllFieldValidationGroup;
import com.example.fams.controller.PasswordValidationGroup;
import com.example.fams.entities.enums.Gender;
import com.example.fams.entities.enums.Role;
import com.example.fams.validation.ValidPhone;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@GroupSequence({UserDTO.class, PasswordValidationGroup.class, AllFieldValidationGroup.class})
public class UserDTO {
    private Long id;

    @NotBlank(message = "First name must not be blank", groups = {AllFieldValidationGroup.class, PasswordValidationGroup.class})
    private String firstName;

    @NotBlank(message = "Last name must not be blank", groups = {AllFieldValidationGroup.class, PasswordValidationGroup.class})
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format", groups = {AllFieldValidationGroup.class, PasswordValidationGroup.class})
    private String email;

    @NotBlank(message = "Password must not be blank", groups = AllFieldValidationGroup.class)
    private String password;

    private String uuid;

    @NotBlank(message = "Phone must not be blank", groups = {AllFieldValidationGroup.class, PasswordValidationGroup.class})
    @Pattern(regexp = "^0\\d{9}$", message = "Phone number must start with 0 and have 10 digits in total", groups = {AllFieldValidationGroup.class, PasswordValidationGroup.class})
    private String phone;

    @NotNull(message = "Date of birth must not be blank", groups = {AllFieldValidationGroup.class, PasswordValidationGroup.class})
    private Long dob;

    private Boolean status;

    @NotNull(message = "Gender cannot be null", groups = {AllFieldValidationGroup.class, PasswordValidationGroup.class})
    private Gender gender;

    private String avatarUrl;

    private List<Long> classIds;

    @NotNull(message = "User Role Id must not be null", groups = {AllFieldValidationGroup.class, PasswordValidationGroup.class})
    private Long userRoleId;
}
