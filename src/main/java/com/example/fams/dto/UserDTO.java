package com.example.fams.dto;

import com.example.fams.entities.UserClass;
import com.example.fams.entities.enums.Role;
import com.example.fams.validation.ValidEmail;
import com.example.fams.validation.ValidPhone;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private String uuid;
    private String phone;
    private Long dob;
    private Boolean status;
    private Boolean gender;
    private List<Long> classIds;
    private Long createdDate;
    private String createBy;
    private String modifiedBy;
    private Long modifiedDate;
}
