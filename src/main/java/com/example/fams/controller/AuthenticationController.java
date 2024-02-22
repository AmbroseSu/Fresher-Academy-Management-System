package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.response.JwtAuthenticationRespone;
import com.example.fams.dto.request.RefreshTokenRequest;
import com.example.fams.dto.request.SignUpRequest;
import com.example.fams.dto.request.SigninRequest;
import com.example.fams.entities.User;
import com.example.fams.services.AuthenticationService;
import com.example.fams.services.impl.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthenticationServiceImpl authen;

    @GetMapping("/testMail")
    public void testMail(){
        authen.generateAndSendOTP("phongdcse171753@fpt.edu.vn");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest signUpRequest){
        try {
            authenticationService.signup(signUpRequest);
        } catch (Exception e) {
            return ResponseUtil.error("null gi do", e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(authenticationService.signup(signUpRequest));
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
