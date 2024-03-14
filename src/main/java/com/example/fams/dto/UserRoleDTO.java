package com.example.fams.dto;

import com.example.fams.entities.UserRole;
import com.example.fams.entities.enums.Permission;
import com.example.fams.entities.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link UserRole}
 */
@Value
@AllArgsConstructor
@Data
public class UserRoleDTO implements Serializable {
    private Long id;
    private Role role;
    private Permission syllabusPermission;
    private Permission materialPermission;
    private Permission trainingProgramPermission;
    private Permission learningObjectivePermission;
    private Permission unitPermission;
    private Permission classPermission;
    private Permission contentPermission;
    private Permission userPermission;
}