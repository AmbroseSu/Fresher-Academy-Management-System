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
    @Query("SELECT u FROM Unit u WHERE u.syllabus.id = :syllabusId")
    List<Unit> findUnitsBySyllabusId(@Param("syllabusId") Long syllabusId);

    @Modifying
    @Transactional
    @Query("UPDATE Content c SET c.unit.id = null WHERE c.unit.id = :unitId")
    void deleteAllContentInUnitByUnitId(Long unitId);

    // ? Search by fields filter
    @Query(value = "SELECT * FROM tbl_unit u " +
            "WHERE (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name,'%'))) AND u.status = TRUE " +
            "AND (:duration IS NULL OR u.duration = :duration)", nativeQuery = true)
    List<Unit> searchSortFilter(@Param("name") String name,
                                @Param("duration") Integer duration,
                                Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM tbl_unit u " +
            "WHERE (:name IS NULL OR  LOWER(u.name) LIKE LOWER(CONCAT('%', :name,'%'))) AND u.status = TRUE " +
            "AND (:duration IS NULL OR u.duration = :duration)", nativeQuery = true)
    Long countSearchSortFilter(@Param("name") String name,
                               @Param("duration") Integer duration);

    @Query(value = "SELECT * FROM tbl_unit u " +
            "WHERE (:name IS NULL OR  LOWER(u.name) LIKE LOWER(CONCAT('%', :name,'%')))" +
            "AND (:duration IS NULL OR u.duration = :duration)"+
            "ORDER BY  " +
            "CASE WHEN :sortById ='iDESC' THEN u.id  END DESC ," +
            "CASE WHEN :sortById ='iASC' THEN u.id  END ASC ,"+
            "CASE WHEN :sortById NOT IN ('iDESC', 'iASC') THEN u.id END DESC", nativeQuery = true)
    List<Unit> searchSortFilterADMIN(@Param("name") String name,
                                     @Param("duration") Integer duration,
                                     @Param("sortById") String sortById,
                                     Pageable pageable);

}
