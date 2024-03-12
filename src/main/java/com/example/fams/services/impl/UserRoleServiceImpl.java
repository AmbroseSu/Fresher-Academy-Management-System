package com.example.fams.services.impl;

import com.example.fams.entities.UserRole;
import com.example.fams.entities.enums.Permission;
import com.example.fams.repository.UserRepository;
import com.example.fams.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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
}
