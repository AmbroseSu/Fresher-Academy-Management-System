package com.example.fams.services.impl;

import com.example.fams.dto.response.JwtAuthenticationRespone;
import com.example.fams.dto.request.RefreshTokenRequest;
import com.example.fams.dto.request.SignUpRequest;
import com.example.fams.dto.request.SigninRequest;
import com.example.fams.entities.User;
import com.example.fams.entities.enums.Role;
import com.example.fams.repository.UserRepository;
import com.example.fams.services.AuthenticationService;
import com.example.fams.services.EmailService;
import com.example.fams.services.JWTService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final Validator validator;


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;

    private final HttpSession httpSession;

    private final EmailService emailService;

    public User signup(SignUpRequest signUpRequest){


        User FAMSuser = new User();
    public ResponseEntity<?> signup(SignUpRequest signUpRequest)  {
        try {

            User FAMSuser = new User();

        FAMSuser.setEmail(signUpRequest.getEmail());
        FAMSuser.setFirstName(signUpRequest.getFirstName());
        FAMSuser.setSecondName(signUpRequest.getLastName());
        FAMSuser.setRole(Role.USER);
        FAMSuser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        FAMSuser.setPhone(signUpRequest.getPhone());

        return userRepository.save(FAMSuser);
            FAMSuser.setEmail(signUpRequest.getEmail());
            FAMSuser.setFirstName(signUpRequest.getFirstName());
            FAMSuser.setSecondName(signUpRequest.getLastName());
            FAMSuser.setRole(Role.USER);
            FAMSuser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            FAMSuser.setPhone(signUpRequest.getPhone());
            return ResponseUtil.getObject(userRepository.save(FAMSuser), HttpStatus.CREATED, "ok");
        }catch (ConstraintViolationException e) {
            return ConstraintViolationExceptionHandler.handleConstraintViolation(e);
        }
    }

    public JwtAuthenticationRespone signin(SigninRequest signinRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(),
                signinRequest.getPassword()));

        var user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(()-> new IllegalArgumentException("Invalid email or password"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        JwtAuthenticationRespone jwtAuthenticationRespone = new JwtAuthenticationRespone();

        jwtAuthenticationRespone.setToken(jwt);
        jwtAuthenticationRespone.setRefreshToken(refreshToken);
        return jwtAuthenticationRespone;
    }

    public JwtAuthenticationRespone refreshToken(RefreshTokenRequest refreshTokenRequest){
        String userEmail = jwtService.extractUserName(refreshTokenRequest.getToken());
        User FAMSuser = userRepository.findByEmail(userEmail).orElseThrow();
        if(jwtService.isTokenValid(refreshTokenRequest.getToken(), FAMSuser)){
            var jwt = jwtService.generateToken(FAMSuser);

            JwtAuthenticationRespone jwtAuthenticationRespone = new JwtAuthenticationRespone();

            jwtAuthenticationRespone.setToken(jwt);
            jwtAuthenticationRespone.setRefreshToken(refreshTokenRequest.getToken());
            return jwtAuthenticationRespone;
        }
        return null;
    }

    public boolean generateAndSendOTP(String userEmail) {
        // Generate a random OTP
        String otp = generateResetToken();

        // Store the OTP in the session or database for verification
        httpSession.setAttribute("otp", otp);
        httpSession.setAttribute("otpUserEmail", userEmail);
        Optional<User> user = userRepository.findByEmail(userEmail);
        if(user.isEmpty()){
            // Send the OTP to the user's email
            emailService.sendOTPByEmail(userEmail, otp);
            return  true;}
        return false;
    }

    private String generateResetToken() {
        long expirationMinutes = 5; // Set the expiration time in minutes
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(expirationMinutes);

        // Encode the expiration time and any other necessary information in the token
        // For simplicity, here we concatenate the token and expiration time

        int otpLength = 6;
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString() + "";
    }

    public boolean verifyOTP(String enteredOTP) {
        // Retrieve stored OTP from the HttpSession
        String storedOTP = (String) httpSession.getAttribute("otp");
        if(storedOTP == null){
            return false;
        }
        if(enteredOTP.equals(storedOTP)){
            httpSession.removeAttribute("otp");
            return true;
        }

        return false;
    }
}
