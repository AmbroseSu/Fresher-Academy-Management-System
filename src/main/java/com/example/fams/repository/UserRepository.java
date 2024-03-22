package com.example.fams.repository;

import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.Unit;
import com.example.fams.entities.User;
import com.example.fams.entities.enums.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "SELECT * FROM tbl_user u " +
            "WHERE (:firstName IS NULL OR  LOWER(u.first_name) LIKE LOWER(CONCAT('%', :firstName,'%')))" +
            "AND (:lastName IS NULL OR  LOWER(u.last_name) LIKE LOWER(CONCAT('%', :lastName,'%')))"+
            "AND (:email IS NULL OR  LOWER(u.email) LIKE LOWER(CONCAT('%', :email,'%')))"+
            "AND (:uuid IS NULL OR  LOWER(u.uuid) LIKE LOWER(CONCAT('%', :uuid,'%')))"+
            "AND (:createBy IS NULL OR  LOWER(u.create_By) LIKE LOWER(CONCAT('%', :createBy,'%')))"+
            "AND (:modifiedBy IS NULL OR  LOWER(u.modified_By) LIKE LOWER(CONCAT('%', :modifiedBy,'%')))"+
            "AND (:phone IS NULL OR  LOWER(u.phone) LIKE LOWER(CONCAT('%', :phone,'%')))"+
            "AND (:dob IS NULL OR u.dob = :dob) " +
            "ORDER BY  " +
            "CASE WHEN :sortById ='iDESC' THEN u.id  END DESC ," +
            "CASE WHEN :sortById ='iASC' THEN u.id  END ASC ,"+
            "CASE WHEN :sortById NOT IN ('iDESC', 'iASC') THEN u.id END DESC", nativeQuery = true)
    List<User> searchSortFilterADMIN(@Param("firstName") String firstName,
                                     @Param("lastName") String lastName,
                                     @Param("email") String email,
                                     @Param("uuid") String uuid,
                                     @Param("createBy") String createBy,
                                     @Param("modifiedBy") String modifiedBy,
                                     @Param("phone") String phone,
                                     @Param("dob") Long dob,
                                     @Param("sortById") String sortById,
                                     Pageable pageable);

    @Query(value = "SELECT * FROM tbl_user u " +
            "WHERE (:firstName IS NULL OR  LOWER(u.first_name) LIKE LOWER(CONCAT('%', :firstName,'%'))) AND u.status = TRUE " +
            "AND (:lastName IS NULL OR  LOWER(u.last_name) LIKE LOWER(CONCAT('%', :lastName,'%')))"+
            "AND (:email IS NULL OR  LOWER(u.email) LIKE LOWER(CONCAT('%', :email,'%')))"+
            "AND (:uuid IS NULL OR  LOWER(u.uuid) LIKE LOWER(CONCAT('%', :uuid,'%')))"+
            "AND (:createBy IS NULL OR  LOWER(u.create_By) LIKE LOWER(CONCAT('%', :createBy,'%')))"+
            "AND (:modifiedBy IS NULL OR  LOWER(u.modified_By) LIKE LOWER(CONCAT('%', :modifiedBy,'%')))"+
            "AND (:phone IS NULL OR  LOWER(u.phone) LIKE LOWER(CONCAT('%', :phone,'%')))"+
            "AND (:dob IS NULL OR u.dob = :dob) " +
            "ORDER BY  " +
            "CASE WHEN :sortById ='iDESC' THEN u.id  END DESC ," +
            "CASE WHEN :sortById ='iASC' THEN u.id  END ASC ,"+
            "CASE WHEN :sortById NOT IN ('iDESC', 'iASC') THEN u.id END DESC", nativeQuery = true)
    List<User> searchSortFilter(@Param("firstName") String firstName,
                                     @Param("lastName") String lastName,
                                     @Param("email") String email,
                                     @Param("uuid") String uuid,
                                     @Param("createBy") String createBy,
                                     @Param("modifiedBy") String modifiedBy,
                                     @Param("phone") String phone,
                                     @Param("dob") Long dob,
                                     @Param("sortById") String sortById,
                                     Pageable pageable);


    @Query(value = "SELECT COUNT(*) FROM tbl_user u " +
            "WHERE (:firstName IS NULL OR  LOWER(u.first_name) LIKE LOWER(CONCAT('%', :firstName,'%')))" +
            "AND (:lastName IS NULL OR  LOWER(u.last_name) LIKE LOWER(CONCAT('%', :lastName,'%')))"+
            "AND (:email IS NULL OR  LOWER(u.email) LIKE LOWER(CONCAT('%', :email,'%')))"+
            "AND (:uuid IS NULL OR  LOWER(u.uuid) LIKE LOWER(CONCAT('%', :uuid,'%')))"+
            "AND (:createBy IS NULL OR  LOWER(u.create_By) LIKE LOWER(CONCAT('%', :createBy,'%')))"+
            "AND (:modifiedBy IS NULL OR  LOWER(u.modified_By) LIKE LOWER(CONCAT('%', :modifiedBy,'%')))"+
            "AND (:phone IS NULL OR  LOWER(u.phone) LIKE LOWER(CONCAT('%', :phone,'%')))"+
            "AND (:dob IS NULL OR u.dob = :dob) ", nativeQuery = true)
    Long countSearchSortFilterADMIN(@Param("firstName") String firstName,
                                     @Param("lastName") String lastName,
                                     @Param("email") String email,
                                     @Param("uuid") String uuid,
                                     @Param("createBy") String createBy,
                                     @Param("modifiedBy") String modifiedBy,
                                     @Param("phone") String phone,
                                     @Param("dob") Long dob);

    @Query(value = "SELECT COUNT(*) FROM tbl_user u " +
            "WHERE (:firstName IS NULL OR  LOWER(u.first_name) LIKE LOWER(CONCAT('%', :firstName,'%'))) AND u.status = TRUE " +
            "AND (:lastName IS NULL OR  LOWER(u.last_name) LIKE LOWER(CONCAT('%', :lastName,'%')))"+
            "AND (:email IS NULL OR  LOWER(u.email) LIKE LOWER(CONCAT('%', :email,'%')))"+
            "AND (:uuid IS NULL OR  LOWER(u.uuid) LIKE LOWER(CONCAT('%', :uuid,'%')))"+
            "AND (:createBy IS NULL OR  LOWER(u.create_By) LIKE LOWER(CONCAT('%', :createBy,'%')))"+
            "AND (:modifiedBy IS NULL OR  LOWER(u.modified_By) LIKE LOWER(CONCAT('%', :modifiedBy,'%')))"+
            "AND (:phone IS NULL OR  LOWER(u.phone) LIKE LOWER(CONCAT('%', :phone,'%')))"+
            "AND (:dob IS NULL OR u.dob = :dob) ", nativeQuery = true)
    Long countSearchSortFilter(@Param("firstName") String firstName,
                                    @Param("lastName") String lastName,
                                    @Param("email") String email,
                                    @Param("uuid") String uuid,
                                    @Param("createBy") String createBy,
                                    @Param("modifiedBy") String modifiedBy,
                                    @Param("phone") String phone,
                                    @Param("dob") Long dob);

    @Transactional
    @Modifying
    User save(User user);
}
