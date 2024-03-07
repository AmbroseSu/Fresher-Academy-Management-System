package com.example.fams.repository;

import com.example.fams.entities.Material;
import com.example.fams.entities.Syllabus;
import com.example.fams.entities.SyllabusMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface SyllabusMaterialRepository extends JpaRepository<SyllabusMaterial, String> {
    SyllabusMaterial findById(Long id);
    @Modifying
    @Transactional
    Integer deleteAllByMaterialId(Long materialId);


    @Query("SELECT s FROM Syllabus s " +
            "JOIN SyllabusMaterial sm ON s.id = sm.syllabus.id " +
            "WHERE sm.material.id = :materialId")

    List<Syllabus> findSyllabusesByMaterialId(Long materialId);

    @Query("SELECT m FROM Material m " +
            "JOIN SyllabusMaterial sm ON m.id = sm.material.id " +
            "WHERE sm.syllabus.id = :syllabusId")
    List<Material> findMaterialBySyllabusesId(Long syllabusId);



    @Query("SELECT sm FROM SyllabusMaterial sm WHERE sm.syllabus.id = :syllabusId")
    List<SyllabusMaterial> findAllMaterialBySyllabusId(Long syllabusId);

    @Modifying
    @Transactional
    void deleteAllBySyllabusId(Long syllabusId);


    @Query("SELECT sm FROM SyllabusMaterial sm where sm.syllabus.id = :syllabusId AND sm.material.id= :materialId")
    SyllabusMaterial findSyllabusMaterialBySyllabusIdAndMaterialId(Long syllabusId, Long materialId);

}
