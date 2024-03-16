package com.example.fams.entities;

import com.example.fams.entities.enums.Gender;
import com.example.fams.entities.enums.Role;
import com.example.fams.validation.ValidEmail;
import com.example.fams.validation.ValidPhone;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


//import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "tbl_user")

public class User extends BaseEntity implements UserDetails {

    private String firstName;

    private String lastName;

    private String email;
    private String password;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private UserRole userRole;

    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;

    private String phone;
    private Long dob;
    private Gender gender;
    private String avatarUrl;

//    @ManyToMany(mappedBy = "user")
//    private List<User> user;
    @OneToMany(mappedBy = "user")
    private List<ClassUser> classUsers;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("syllabus:" + userRole.getSyllabusPermission().name()),
                new SimpleGrantedAuthority("material:" + userRole.getMaterialPermission().name()),
                new SimpleGrantedAuthority("trainingProgram:" + userRole.getTrainingProgramPermission().name()),
                new SimpleGrantedAuthority("learningObjective:" + userRole.getLearningObjectivePermission().name()),
                new SimpleGrantedAuthority("unit:" + userRole.getUnitPermission().name()),
                new SimpleGrantedAuthority("class:" + userRole.getClassPermission().name()),
                new SimpleGrantedAuthority("content:" + userRole.getContentPermission().name()),
                new SimpleGrantedAuthority("user:" + userRole.getUserPermission().name())
        );
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return super.isStatus();
    }
}
