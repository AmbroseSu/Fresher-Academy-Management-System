package com.example.fams.repository;

import com.example.fams.entities.Content;
import com.example.fams.entities.Syllabus;
import com.example.fams.entities.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, String> {
    List<Unit> findByStatusIsTrue(Pageable pageable);
    List<Unit> findAllBy(Pageable pageable);
    Unit findById(Long id);
    Unit findByStatusIsTrueAndId(Long id);

    @Query("SELECT c FROM Content c WHERE c.unit.id = :unitId")
    List<Content> findContentsByUnitId(@Param("unitId") Long unitId);
    @Query("SELECT u.syllabus FROM Unit u WHERE u.syllabus.id = :syllabusId")
    Syllabus findSyllabusBySyllabusId(@Param("syllabusId") Long syllabusId);

    @Modifying
    @Transactional
    @Query("UPDATE Content c SET c.unit.id = null WHERE c.unit.id = :unitId")
    void deleteAllContentInUnitByUnitId(Long unitId);

    // ? Search by fields filter
    @Query("SELECT unit FROM Unit unit " +
            "WHERE (:name IS NULL OR unit.name = :name) AND unit.status = TRUE " +
            "AND (:duration IS NULL OR unit.duration = :duration)")
    List<Unit> searchSortFilter(@Param("name") String name,
                                             @Param("duration") Integer duration,
                                             Pageable pageable);

    @Query("SELECT COUNT(unit) FROM Unit unit " +
            "WHERE (:name IS NULL OR unit.name = :name) AND unit.status = TRUE " +
            "AND (:duration IS NULL OR unit.duration = :duration)")
    Long countSearchSortFilter(@Param("name") String name,
                                @Param("duration") Integer duration);


    @Query("SELECT unit FROM Unit unit " +
            "WHERE (:name IS NULL OR unit.name = :name)" +
            "AND (:duration IS NULL OR unit.duration = :duration)"+
            "ORDER BY  " +
            "CASE WHEN :sortById ='iDESC' THEN unit.id  END DESC ," +
            "CASE WHEN :sortById ='iASC' THEN unit.id  END ASC ,"+
            "unit.id desc")
    List<Unit> searchSortFilterADMIN(@Param("name") String name,
                                @Param("duration") Integer duration,
                                     @Param("sortById") String sortById,
                                Pageable pageable);


}
