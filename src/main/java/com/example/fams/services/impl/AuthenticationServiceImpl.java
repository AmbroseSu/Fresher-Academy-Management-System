package com.example.fams.services.impl;

import com.example.fams.dto.JwtAuthenticationRespone;
import com.example.fams.dto.RefreshTokenRequest;
import com.example.fams.dto.SignUpRequest;
import com.example.fams.dto.SigninRequest;
import com.example.fams.entities.FAMS_user;
import com.example.fams.entities.enums.Role;
import com.example.fams.repository.UserRepository;
import com.example.fams.services.AuthenticationService;
import com.example.fams.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;

    public FAMS_user signup(SignUpRequest signUpRequest){
        FAMS_user FAMSuser = new FAMS_user();
        FAMSuser.setEmail(signUpRequest.getEmail());
        FAMSuser.setFirstName(signUpRequest.getFirstName());
        FAMSuser.setSecondName(signUpRequest.getLastName());
        FAMSuser.setRole(Role.USER);
        FAMSuser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        return userRepository.save(FAMSuser);
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
        FAMS_user FAMSuser = userRepository.findByEmail(userEmail).orElseThrow();
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
