package com.example.fams.entities;

import com.example.fams.entities.enums.Permission;
import com.example.fams.entities.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "tbl_userRole")
public class UserRole implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "userRole", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<User> users;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Permission syllabusPermission;

    @Enumerated(EnumType.STRING)
    private Permission materialPermission;

    @Enumerated(EnumType.STRING)
    private Permission trainingProgramPermission;

    @Enumerated(EnumType.STRING)
    private Permission learningObjectivePermission;

    @Enumerated(EnumType.STRING)
    private Permission unitPermission;

    @Enumerated(EnumType.STRING)
    private Permission classPermission;

    @Enumerated(EnumType.STRING)
    private Permission contentPermission;

    @Enumerated(EnumType.STRING)
    private Permission userPermission;

    public UserRole(Role role, Permission syllabusPermission, Permission materialPermission, Permission trainingProgramPermission, Permission learningObjectivePermission, Permission unitPermission, Permission classPermission, Permission contentPermission, Permission userPermission) {
        this.role = role;
        this.syllabusPermission = syllabusPermission;
        this.materialPermission = materialPermission;
        this.trainingProgramPermission = trainingProgramPermission;
        this.learningObjectivePermission = learningObjectivePermission;
        this.unitPermission = unitPermission;
        this.classPermission = classPermission;
        this.contentPermission = contentPermission;
        this.userPermission = userPermission;
    }
}
