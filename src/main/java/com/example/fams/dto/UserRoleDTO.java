package com.example.fams.dto;

import com.example.fams.entities.UserRole;
import com.example.fams.entities.enums.Permission;
import com.example.fams.entities.enums.Role;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^(Access_Denied|View|Create|Modify|Full_Access)$", message = "Permission must be one of the following: Access_Denied, View, Create, Modify, Full_Access")
    private Permission syllabusPermission;
    @Pattern(regexp = "^(Access_Denied|View|Create|Modify|Full_Access)$", message = "Permission must be one of the following: Access_Denied, View, Create, Modify, Full_Access")
    private Permission materialPermission;
    @Pattern(regexp = "^(Access_Denied|View|Create|Modify|Full_Access)$", message = "Permission must be one of the following: Access_Denied, View, Create, Modify, Full_Access")
    private Permission trainingProgramPermission;
    @Pattern(regexp = "^(Access_Denied|View|Create|Modify|Full_Access)$", message = "Permission must be one of the following: Access_Denied, View, Create, Modify, Full_Access")
    private Permission learningObjectivePermission;
    @Pattern(regexp = "^(Access_Denied|View|Create|Modify|Full_Access)$", message = "Permission must be one of the following: Access_Denied, View, Create, Modify, Full_Access")
    private Permission unitPermission;
    @Pattern(regexp = "^(Access_Denied|View|Create|Modify|Full_Access)$", message = "Permission must be one of the following: Access_Denied, View, Create, Modify, Full_Access")
    private Permission classPermission;
    @Pattern(regexp = "^(Access_Denied|View|Create|Modify|Full_Access)$", message = "Permission must be one of the following: Access_Denied, View, Create, Modify, Full_Access")
    private Permission contentPermission;
    @Pattern(regexp = "^(Access_Denied|View|Create|Modify|Full_Access)$", message = "Permission must be one of the following: Access_Denied, View, Create, Modify, Full_Access")
    private Permission userPermission;
}