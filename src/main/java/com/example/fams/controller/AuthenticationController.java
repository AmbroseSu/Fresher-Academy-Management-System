package com.example.fams.controller;

import com.example.fams.config.ConstraintViolationExceptionHandler;
import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.response.JwtAuthenticationRespone;
import com.example.fams.dto.request.RefreshTokenRequest;
import com.example.fams.dto.request.SignUpRequest;
import com.example.fams.dto.request.SigninRequest;
import com.example.fams.entities.User;
import com.example.fams.services.AuthenticationService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signUpRequest){
            return authenticationService.signup(signUpRequest);
    }



    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationRespone> signin (@RequestBody SigninRequest signinRequest){
        return ResponseEntity.ok(authenticationService.signin(signinRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationRespone> refresh (@RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }
}
