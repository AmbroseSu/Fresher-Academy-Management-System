package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.UserDTO;
import com.example.fams.dto.response.JwtAuthenticationRespone;
import com.example.fams.dto.request.RefreshTokenRequest;
import com.example.fams.dto.request.SigninRequest;
import com.example.fams.entities.FamsClass;
import com.example.fams.entities.OtpToken;
import com.example.fams.entities.User;
import com.example.fams.repository.ClassUserRepository;
import com.example.fams.repository.OtpTokenRepository;
import com.example.fams.repository.UserRepository;
import com.example.fams.services.AuthenticationService;
import com.example.fams.services.EmailService;
import com.example.fams.services.JWTService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final int OTP_LENGTH = 6;

    private static final long EXPIRATION_MINUTES = 3;

    private final UserRepository userRepository;

    private final GenericConverter genericConverter;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;

    private final HttpSession httpSession;

    private final EmailService emailService;

    private final ClassUserRepository classUserRepository;

    private final OtpTokenRepository otpTokenRepository;


    public ResponseEntity<?> signin(SigninRequest signinRequest) {
        // * method authenticate() của AuthenticationManager dùng để tạo ra một object Authentication object
        // ? Với UsernamePasswordAuthenticationToken là class implements từ Authentication, đại diện cho 1 authentication object
        // todo Trả về một object Authentication và đưa vào Security Context để quản lý
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(),
                signinRequest.getPassword()));

        var user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        JwtAuthenticationRespone jwtAuthenticationRespone = new JwtAuthenticationRespone();

        UserDTO userDTO = convertUserToUserDTO(user);

        jwtAuthenticationRespone.setUserDTO(userDTO);
        jwtAuthenticationRespone.setToken(jwt);
        jwtAuthenticationRespone.setRefreshToken(refreshToken);
        return ResponseUtil.getObject(jwtAuthenticationRespone, HttpStatus.OK, "Sign in successfully");
    }

    public ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.extractUserName(refreshTokenRequest.getToken());
        User FAMSuser = userRepository.findByEmail(userEmail).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), FAMSuser)) {
            var jwt = jwtService.generateToken(FAMSuser);

            JwtAuthenticationRespone jwtAuthenticationRespone = new JwtAuthenticationRespone();

            jwtAuthenticationRespone.setToken(jwt);
            jwtAuthenticationRespone.setRefreshToken(refreshTokenRequest.getToken());
            return ResponseUtil.getObject(jwtAuthenticationRespone, HttpStatus.OK, "Token sent successfully");
        }
        return null;
    }

    public ResponseEntity<?> generateAndSendOTP(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent()) {
            // Generate a random OTP
            String otp = generateOTP();
            OtpToken otpToken = otpTokenRepository.findByUserId(user.get().getId());
            if (otpToken != null) {
                otpTokenRepository.delete(otpToken);
            }
            OtpToken newOtpToken = new OtpToken();
            newOtpToken.setUserId(user.get().getId());
            newOtpToken.setOtpSecret(otp);
            newOtpToken.setCreationTime(LocalDateTime.now());
            newOtpToken.setExpirationTime(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
            otpTokenRepository.save(newOtpToken);
            emailService.sendOTPByEmail(userEmail, otp);
            return ResponseUtil.getObject(null, HttpStatus.OK, "OTP sent successfully");
        }
        return ResponseUtil.error("Cannot send email", "Email does not exists", HttpStatus.NOT_ACCEPTABLE);
    }

    private static String generateOTP() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public ResponseEntity<?> verifyOTP(String enteredOTP, Long requestId) {
        OtpToken otpToken = otpTokenRepository.findByUserId(requestId);
        if (otpToken != null) {
            String storedOTP = otpToken.getOtpSecret();
            LocalDateTime expirationTime = otpToken.getExpirationTime();
            if(enteredOTP.equals(storedOTP)){
                if (LocalDateTime.now().isBefore(expirationTime)) {
                    otpTokenRepository.deleteByUserId(requestId);
                    return ResponseUtil.getObject(null, HttpStatus.OK, "Valid OTP");
                }
                return ResponseUtil.error("Invalid OTP", "OTP Expired", HttpStatus.NOT_ACCEPTABLE);
            }
        }
        return ResponseUtil.error("Invalid OTP", "Invalid OTP", HttpStatus.NOT_ACCEPTABLE);
    }

    public ResponseEntity<?> resetPassword(String email, String newPassword) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            user.get().setPassword(passwordEncoder.encode(newPassword));
            User newUser = userRepository.save(user.get());
            UserDTO userDTO = convertUserToUserDTO(newUser);
            return ResponseUtil.getObject(userDTO, HttpStatus.OK, "Password changed successfully");
        }
        return ResponseUtil.error("User not found", "Cannot reset password", HttpStatus.BAD_REQUEST);
    }

    private UserDTO convertUserToUserDTO(User entity) {
        UserDTO newUserDTO = (UserDTO) genericConverter.toDTO(entity, UserDTO.class);
        List<FamsClass> classes = classUserRepository.findClassByUserId(entity.getId());
        if (entity.getClassUsers() == null){
            newUserDTO.setClassIds(null);
        }
        else {
            // ! Set list learningObjectiveIds và unitId sau khi convert ở trên vào contentDTO
            List<Long> classIds = classes.stream()
                    .map(FamsClass::getId)
                    .toList();

            newUserDTO.setClassIds(classIds);

        }
        return newUserDTO;
    }

    @Scheduled(fixedRate = 3600000) // Run every hour
    private void deleteExpiredTokens() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        otpTokenRepository.deleteExpiredTokens(currentDateTime);
    }

}