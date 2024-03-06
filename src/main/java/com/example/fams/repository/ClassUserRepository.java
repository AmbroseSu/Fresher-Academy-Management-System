package com.example.fams.repository;

import com.example.fams.entities.ClassUser;
import com.example.fams.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ClassUserRepository extends JpaRepository<ClassUser, String> {
  @Modifying
  @Transactional
  Integer deleteAllByFamsClassId(Long classId);
  ClassUser findById(Long id);

  @Query("SELECT clu FROM User u " +
      "JOIN ClassUser clu ON u.id = clu.user.id " +
      "WHERE clu.famsClass.id = :classId AND clu.user.id = :userId")
  ClassUser findByClassIdAndUserId(Long classId, Long userId);
  @Query("SELECT u FROM User u " +
      "JOIN ClassUser clu ON u.id = clu.user.id " +
      "WHERE clu.famsClass.id = :classId ")
  List<User> findUserByClassId(Long classId);
}
