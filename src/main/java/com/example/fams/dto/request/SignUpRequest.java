package com.example.fams.dto.request;

import com.example.fams.validation.ValidEmail;
import com.example.fams.validation.ValidPhone;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Khong dung den nua
@Data
public class SignUpRequest {
    @NotBlank(message = "firstName must not be blank")
    private String firstName;
    @NotBlank(message = "lastName must not be blank")
    private String lastName;
    @ValidEmail
    private String email;
    @NotBlank
    private String password;
    @ValidPhone
    private String phone;
}
