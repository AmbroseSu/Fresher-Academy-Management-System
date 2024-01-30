package com.example.fams.dto;

import lombok.Data;

@Data
public class JwtAuthenticationRespone {

    private String token;
    private String refreshToken;
}
