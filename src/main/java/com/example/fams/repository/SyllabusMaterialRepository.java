package com.example.fams.repository;

import com.example.fams.entities.Syllabus;
import com.example.fams.entities.SyllabusMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface SyllabusMaterialRepository extends JpaRepository<SyllabusMaterial, Long> {
    @Modifying
    @Transactional
    Integer deleteAllByMaterialId(Long materialId);

    @Query("SELECT s FROM Syllabus s " +
            "JOIN SyllabusMaterial sm ON s.id = sm.syllabus.id " +
            "WHERE sm.material.id = :materialId")

    List<Syllabus> findSyllabusesByMaterialId(Long materialId);
}
