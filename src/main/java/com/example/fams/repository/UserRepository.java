package com.example.fams.repository;

import com.example.fams.entities.FAMS_user;
import com.example.fams.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<FAMS_user, Long> {

    Optional<FAMS_user> findByEmail(String email);
    FAMS_user findByRole(Role role);

}
