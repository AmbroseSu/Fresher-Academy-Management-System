package com.example.fams.repository;

import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.User;
import com.example.fams.entities.enums.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    User findByRole(Role role);

    List<User> findAllByStatusIsTrue(Pageable pageable);

    List<User> findAllByOrderByIdDesc(Pageable pageable);

    boolean existsByEmail(String email);

    User findByStatusIsTrueAndId(Long id);

    User findByStatusIsTrueAndUuid(String uuid);
}
