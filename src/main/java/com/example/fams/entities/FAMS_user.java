package com.example.fams.entities;

import com.example.fams.entities.enums.Role;
import com.example.fams.validation.CustomerUUIDGenerator;
import com.example.fams.validation.ValidEmail;
import com.example.fams.validation.ValidPhone;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class FAMS_user implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String firstName;

    @NotBlank
    private String secondName;

    @ValidEmail
    private String email;
    private String password;
    private Role role;

    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "com.example.fams.validation.CustomerUUIDGenerator"
    )
    private String uuid;

    @ValidPhone
    private String phone;
    private Long dob;
    private Boolean gender;
    private Integer status;
    private String createBy;
    private Long createDate;
    private String modifiedBy;
    private Long modifiedDate;

    @ManyToMany(mappedBy = "users")
    private List<Class> classes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
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
        return true;
    }
}
