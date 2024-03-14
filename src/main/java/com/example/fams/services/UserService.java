package com.example.fams.services;

import com.example.fams.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends IGenericService<UserDTO> {
    UserDetailsService userDetailsService();

    ResponseEntity<?> findByUuid(String uuid);

    ResponseEntity<?> updateUserImage(Long id, String avatarUrl);
}
