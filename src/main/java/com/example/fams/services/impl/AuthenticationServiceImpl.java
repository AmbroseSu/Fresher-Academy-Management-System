package com.example.fams.services.impl;

import com.example.fams.config.ConstraintViolationExceptionHandler;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.UpsertUserDTO;
import com.example.fams.dto.UserDTO;
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
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final int OTP_LENGTH = 6;

    private static final long EXPIRATION_MINUTES = 3;

    private final Validator validator;

    private final UserRepository userRepository;

    private final GenericConverter genericConverter;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;

    private final HttpSession httpSession;

    private final EmailService emailService;

    public ResponseEntity<?> signup(User user) {
        try {
            // check if user already exists
            if (userRepository.existsByEmail(user.getEmail())) {
                return ResponseUtil.error("Email is already in use","Sign up failed", HttpStatus.BAD_REQUEST);
            }

        user.setRole(Role.USER);
            user.setUuid(UUID.randomUUID().toString());
            UpsertUserDTO result = (UpsertUserDTO) genericConverter.toDTO(user, UpsertUserDTO.class);
            userRepository.save(user);

            return ResponseUtil.getObject(result, HttpStatus.CREATED, "ok");
        } catch (ConstraintViolationException e) {
            return ConstraintViolationExceptionHandler.handleConstraintViolation(e);
        }
    }

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

        try {
            // Generate a random OTP
            String otp = generateOTP();

            // Store the OTP in the session or database for verification
            httpSession.setAttribute("otp", otp);
            httpSession.setAttribute("otpUserEmail", userEmail);
            httpSession.setAttribute("expirationTime", LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
            emailService.sendOTPByEmail(userEmail, otp);
            return ResponseUtil.getObject(null, HttpStatus.OK, "OTP sent successfully");
//        } catch (MailException ex) {
//            return ResponseUtil.error("Cannot send OTP", ex.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (Exception ex) {
//            return ResponseUtil.error("Cannot send OTP", ex.getMessage(), HttpStatus.BAD_REQUEST);
//        }
    }

    private static String generateOTP() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public ResponseEntity<?> verifyOTP(String enteredOTP) {
        String storedOTP = (String) httpSession.getAttribute("otp");
        LocalDateTime expirationTime = (LocalDateTime) httpSession.getAttribute("expirationTime");
        if(storedOTP == null){
            ResponseUtil.getObject(null, HttpStatus.OK, "Valid OTP");
        }
        if(enteredOTP.equals(storedOTP)){
            if (LocalDateTime.now().isBefore(expirationTime)) {
                httpSession.removeAttribute("otp");
                httpSession.removeAttribute("otpUserEmail");
                httpSession.removeAttribute("expirationTime");
                ResponseUtil.getObject(null, HttpStatus.OK, "Valid OTP");
            } else {
                return ResponseUtil.error("Invalid OTP", "OTP Expired", HttpStatus.NOT_ACCEPTABLE);
            }
        }
        return ResponseUtil.error("Invalid OTP", "Invalid OTP", HttpStatus.NOT_ACCEPTABLE);
    }

    public ResponseEntity<?> resetPassword(String email, String newPassword) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            user.get().setPassword(passwordEncoder.encode(newPassword));
            return ResponseUtil.getObject(userRepository.save(user.get()), HttpStatus.OK, "Password changed successfully");
        }
        return ResponseUtil.error("User not found", "Cannot reset password", HttpStatus.BAD_REQUEST);
    }
}
