package com.example.fams.services;

public interface EmailService {

    void sendOTPByEmail(String userEmail, String otp);
}
