package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.entities.UserRole;
import com.example.fams.entities.enums.Permission;
import com.example.fams.entities.enums.Role;
import com.example.fams.repository.UserRepository;
import com.example.fams.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public void updateUserRoleByUserId(Long userId, Permission syllabusPermission,
                                           Permission materialPermission,
                                           Permission trainingProgramPermission,
                                           Permission learningObjectivePermission,
                                           Permission unitPermission,
                                           Permission classPermission,
                                           Permission contentPermission,
                                           Permission userPermission){
        userRoleRepository.updateUserRoleByUserId(userId, syllabusPermission, materialPermission, trainingProgramPermission, learningObjectivePermission, unitPermission, classPermission, contentPermission, userPermission);
    }

    public ResponseEntity<?> findAllUserRole(){
        List<UserRole> userRole = userRoleRepository.findAllBy();
        List<Role> roles = userRole.stream()
                .map(UserRole::getRole)
                .toList();
        return ResponseUtil.getCollection(roles,
                HttpStatus.OK,
                "Fetched successfully", 1, 10, 3);
    }
}
