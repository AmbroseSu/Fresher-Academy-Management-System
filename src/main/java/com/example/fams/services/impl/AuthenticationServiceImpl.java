package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.response.JwtAuthenticationRespone;
import com.example.fams.dto.request.RefreshTokenRequest;
import com.example.fams.dto.request.SignUpRequest;
import com.example.fams.dto.request.SigninRequest;
import com.example.fams.entities.User;
import com.example.fams.entities.enums.Role;
import com.example.fams.repository.UserRepository;
import com.example.fams.services.AuthenticationService;
import com.example.fams.services.JWTService;
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

import java.util.HashMap;
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

    public ResponseEntity<?> signup(SignUpRequest signUpRequest)  {
    try {

        User FAMSuser = new User();

        FAMSuser.setEmail(signUpRequest.getEmail());
        FAMSuser.setFirstName(signUpRequest.getFirstName());
        FAMSuser.setSecondName(signUpRequest.getLastName());
        FAMSuser.setRole(Role.USER);
        FAMSuser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        FAMSuser.setPhone(signUpRequest.getPhone());
        return ResponseUtil.getObject(userRepository.save(FAMSuser), HttpStatus.CREATED, "ok");
    }catch (ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        List<String> errorMessages = violations.stream()
            .map(violation -> String.format("%s %s", violation.getPropertyPath(), violation.getMessage()))
            .collect(Collectors.toList());

        String detailedErrorMessage = String.join(", ", errorMessages);
        String userFriendlyMessage = errorMessages.isEmpty() ? "Validation failed" : errorMessages.get(0);

        return ResponseUtil.error( detailedErrorMessage,"Bad request", HttpStatus.BAD_REQUEST);
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
}
