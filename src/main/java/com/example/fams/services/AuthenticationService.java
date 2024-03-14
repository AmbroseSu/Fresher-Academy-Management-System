package com.example.fams.services;

import com.example.fams.dto.response.JwtAuthenticationRespone;
import com.example.fams.dto.request.RefreshTokenRequest;
import com.example.fams.dto.request.SignUpRequest;
import com.example.fams.dto.request.SigninRequest;
import com.example.fams.entities.User;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface AuthenticationService {
    ResponseEntity<?> signin(SigninRequest signinRequest);
    ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest);
    ResponseEntity<?> generateAndSendOTP(String userEmail);
    ResponseEntity<?> verifyOTP(String enteredOTP, Long id);
    ResponseEntity<?> resetPassword(String email, String newPassword);
}
