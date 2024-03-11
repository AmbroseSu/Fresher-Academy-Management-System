package com.example.fams.services;

import jakarta.mail.MessagingException;

public interface EmailService {

    void sendOTPByEmail(String userEmail, String otp);
    void sendWelcomeEmail(String userEmail, String name, String password) throws MessagingException;
}
