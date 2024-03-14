package com.example.fams.entities;

import com.example.fams.validation.NotBlankOrNull;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class OtpToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private Long userId;
    @NotBlankOrNull
    private String otpSecret;
    private LocalDateTime creationTime;
    private LocalDateTime expirationTime;
}
