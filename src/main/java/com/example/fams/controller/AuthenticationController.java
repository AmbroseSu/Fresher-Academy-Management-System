package com.example.fams.controller;

import com.example.fams.dto.request.ResetPasswordRequest;
import com.example.fams.dto.request.RefreshTokenRequest;
import com.example.fams.dto.request.SigninRequest;
import com.example.fams.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {
    private final AuthenticationService authenticationService;

//    @GetMapping("/testMail")
//    public void testMail(){
//        authen.generateAndSendOTP("phongdcse171753@fpt.edu.vn");
//    }

    // !Không dùng nữa
//    @PostMapping("/signup")
//    public ResponseEntity<?> signup(@Valid @RequestBody User user){
//            return authenticationService.signup(user);
//    }

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
    public ResponseEntity<?> sendMailOTP(@RequestParam String email) {
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
