package com.example.fams.repository;

import com.example.fams.entities.ClassUser;
import com.example.fams.entities.FamsClass;
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

  @Modifying
  @Transactional
  Integer deleteAllByUserId(Long userId);

  ClassUser findById(Long id);

  @Query("SELECT fc FROM FamsClass fc " +
      "JOIN ClassUser clu ON fc.id = clu.famsClass.id " +
      "WHERE clu.user.id = :userId")
  List<FamsClass> findClassByUserId(Long userId);
  @Query("SELECT u FROM User u " +
      "JOIN ClassUser clu ON u.id = clu.user.id " +
      "WHERE clu.famsClass.id = :classId ")
  List<User> findUserByClassId(Long classId);
}
