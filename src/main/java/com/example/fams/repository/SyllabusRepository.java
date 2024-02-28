package com.example.fams.repository;

import com.example.fams.entities.Syllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Repository
public interface SyllabusRepository extends JpaRepository<Syllabus, String> {
    List<Syllabus> findByStatusIsTrue();
    Syllabus findById(Long id);

    Optional<Syllabus> findByName(String name);
    Syllabus findByStatusIsTrueAndCode(String code);
    Syllabus findByStatusIsTrueAndId(Long id);
    @Transactional
    @Modifying
    @Query("UPDATE Syllabus lo SET lo.status = ?1 WHERE lo.id = ?2")
    Syllabus changeStatus(Boolean status, Long id);

}
