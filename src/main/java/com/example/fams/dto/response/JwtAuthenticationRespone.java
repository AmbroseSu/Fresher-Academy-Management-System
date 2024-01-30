package com.example.fams.dto.response;

import lombok.Data;

@Data
public class JwtAuthenticationRespone {

    private String token;
    private String refreshToken;
}
