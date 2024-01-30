package com.example.fams.services;

import com.example.fams.dto.JwtAuthenticationRespone;
import com.example.fams.dto.RefreshTokenRequest;
import com.example.fams.dto.SignUpRequest;
import com.example.fams.dto.SigninRequest;
import com.example.fams.entities.User;

public interface AuthenticationService {
    User signup(SignUpRequest signUpRequest);
    JwtAuthenticationRespone signin(SigninRequest signinRequest);
    JwtAuthenticationRespone refreshToken(RefreshTokenRequest refreshTokenRequest);
}
