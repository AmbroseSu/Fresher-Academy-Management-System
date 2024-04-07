package com.example.fams.repository;

import com.example.fams.entities.UserRole;
import com.example.fams.entities.enums.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE UserRole ur SET ur.syllabusPermission = :syllabusPermission, ur.materialPermission = :materialPermission, ur.trainingProgramPermission = :trainingProgramPermission, ur.learningObjectivePermission = :learningObjectivePermission, ur.unitPermission = :unitPermission, ur.classPermission = :classPermission, ur.contentPermission = :contentPermission, ur.userPermission = :userPermission WHERE ur.id = :userRoleId")
    void updateUserRoleByUserRoleId(@Param("userRoleId") Long userRoleId,
            @Param("syllabusPermission") Permission syllabusPermission,
            @Param("materialPermission") Permission materialPermission,
            @Param("trainingProgramPermission") Permission trainingProgramPermission,
            @Param("learningObjectivePermission") Permission learningObjectivePermission,
            @Param("unitPermission") Permission unitPermission, @Param("classPermission") Permission classPermission,
            @Param("contentPermission") Permission contentPermission,
            @Param("userPermission") Permission userPermission);

    UserRole findFirstByOrderByIdAsc();

    @Query("SELECT u.userRole FROM User u WHERE u.id = :userId")
    UserRole findUserRoleByUserId(@Param("userId") Long userId);

    List<UserRole> findAllBy();

}