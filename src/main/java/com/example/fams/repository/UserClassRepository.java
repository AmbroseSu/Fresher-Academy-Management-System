package com.example.fams.repository;

import com.example.fams.entities.FamsClass;
import com.example.fams.entities.Content;
import com.example.fams.entities.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserClassRepository extends JpaRepository<UserClass, Long> {

    @Query("SELECT c FROM FamsClass c " +
            "JOIN UserClass uc ON c.id = uc.aClass.id " +
            "WHERE uc.user.id = :userId")
    List<Class> findClassesByUserId(Long userId);
}
