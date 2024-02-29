package com.example.fams.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    String email;
    String newPassword;
}
