package com.example.fams.controller;

import com.example.fams.config.ConstraintViolationExceptionHandler;
import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.request.ResetPasswordRequest;
import com.example.fams.dto.response.JwtAuthenticationRespone;
import com.example.fams.dto.request.RefreshTokenRequest;
import com.example.fams.dto.request.SignUpRequest;
import com.example.fams.dto.request.SigninRequest;
import com.example.fams.entities.User;
import com.example.fams.services.AuthenticationService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import com.example.fams.services.impl.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

//    @GetMapping("/testMail")
//    public void testMail(){
//        authen.generateAndSendOTP("phongdcse171753@fpt.edu.vn");
//    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signUpRequest){
        return authenticationService.signup(signUpRequest);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin (@RequestBody SigninRequest signinRequest){
        return authenticationService.signin(signinRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh (@RequestBody RefreshTokenRequest refreshTokenRequest){
        return authenticationService.refreshToken(refreshTokenRequest);
    }

//    @PostMapping("/sendOTP")
//    public ResponseEntity<?> sendMailOTP(@RequestParam String email){
//        authenticationService.generateAndSendOTP(email);
//        return ResponseEntity.ok("OTP sent successfully");
//    }
    @PostMapping("/sendOTP")
    public ResponseEntity<?> sendMailOTP(@RequestParam String email){
        return authenticationService.generateAndSendOTP(email);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateOTP(@RequestParam String otp){
        return authenticationService.verifyOTP(otp);
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        return authenticationService.resetPassword(request.getEmail(), request.getNewPassword());
    }

}
