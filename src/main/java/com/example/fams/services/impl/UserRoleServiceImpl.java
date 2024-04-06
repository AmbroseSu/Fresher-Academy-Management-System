package com.example.fams.services.impl;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.UserRoleDTO;
import com.example.fams.entities.UserRole;
import com.example.fams.entities.enums.Permission;
import com.example.fams.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl {
    private final UserRoleRepository userRoleRepository;

    public ResponseEntity<?> updateUserRoleByUserRoleId(Long userRoleId, Permission syllabusPermission,
            Permission materialPermission,
            Permission trainingProgramPermission,
            Permission learningObjectivePermission,
            Permission unitPermission,
            Permission classPermission,
            Permission contentPermission,
            Permission userPermission) {
        userRoleRepository.updateUserRoleByUserRoleId(userRoleId, syllabusPermission, materialPermission,
                trainingProgramPermission, learningObjectivePermission, unitPermission, classPermission,
                contentPermission, userPermission);

        Optional<UserRole> userRoleOptional = userRoleRepository.findById(userRoleId);
        if (userRoleOptional.isPresent()) {
            UserRole userRole = userRoleOptional.get();
            UserRoleDTO userRoleDTO = new UserRoleDTO(userRole.getId(), userRole.getRole(),
                    userRole.getSyllabusPermission(), userRole.getMaterialPermission(),
                    userRole.getTrainingProgramPermission(), userRole.getLearningObjectivePermission(),
                    userRole.getUnitPermission(), userRole.getClassPermission(), userRole.getContentPermission(),
                    userRole.getUserPermission());
            return ResponseUtil.getObject(userRoleDTO,
                    HttpStatus.OK,
                    "Update User Permission successfully");
        } else {
            return ResponseUtil.getObject(null,
                    HttpStatus.NOT_FOUND,
                    "User Role not found");
        }
    }

    public ResponseEntity<?> findAllUserRole() {
        List<UserRole> userRoles = userRoleRepository.findAllBy();
        List<UserRoleDTO> userRoleDTOs = userRoles.stream()
                .map(userRole -> {
                    UserRoleDTO userRoleDTO = new UserRoleDTO(userRole.getId(), userRole.getRole(),
                            userRole.getSyllabusPermission(),
                            userRole.getMaterialPermission(),
                            userRole.getTrainingProgramPermission(),
                            userRole.getLearningObjectivePermission(),
                            userRole.getUnitPermission(), userRole.getClassPermission(),
                            userRole.getContentPermission(), userRole.getUserPermission());
                    return userRoleDTO;
                })
                .toList();
        return ResponseUtil.getCollection(userRoleDTOs,
                HttpStatus.OK,
                "Fetched successfully", 1, 10, 3);
    }
}
