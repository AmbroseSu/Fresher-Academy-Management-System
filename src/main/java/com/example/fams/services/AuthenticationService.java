package com.example.fams.services;

import com.example.fams.dto.response.JwtAuthenticationRespone;
import com.example.fams.dto.request.RefreshTokenRequest;
import com.example.fams.dto.request.SignUpRequest;
import com.example.fams.dto.request.SigninRequest;
import com.example.fams.entities.User;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<?> signup(SignUpRequest signUpRequest);
    JwtAuthenticationRespone signin(SigninRequest signinRequest);
    JwtAuthenticationRespone refreshToken(RefreshTokenRequest refreshTokenRequest);
}
