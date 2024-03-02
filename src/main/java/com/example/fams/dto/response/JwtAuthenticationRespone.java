package com.example.fams.dto.response;

import com.example.fams.dto.UserDTO;
import lombok.Data;

@Data
public class JwtAuthenticationRespone {

    private UserDTO user;
    private String token;
    private String refreshToken;
}
