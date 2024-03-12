package com.example.fams.repository;

import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.User;
import com.example.fams.entities.enums.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    User findById(Long id);

    List<User> findAllByStatusIsTrue(Pageable pageable);

    List<User> findAllByOrderByIdDesc(Pageable pageable);

    boolean existsByEmail(String email);

    User findByStatusIsTrueAndId(Long id);

    User findByStatusIsTrueAndUuid(String uuid);

    @Transactional
    @Modifying
    User save(User user);
}
