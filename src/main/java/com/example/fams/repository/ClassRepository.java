package com.example.fams.repository;

import com.example.fams.entities.FamsClass;
import com.example.fams.entities.LearningObjective;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ClassRepository extends JpaRepository<FamsClass, String> {
  List<FamsClass> findAllByStatusIsTrue(Pageable pageable);
  List<FamsClass> findAllByOrderByIdDesc(Pageable pageable);
  FamsClass findById(Long id);
  FamsClass findByStatusIsTrueAndId(Long id);
  FamsClass findByStatusIsTrueAndCode(String code);
  Long countAllByStatusIsTrue();
  @Transactional
  @Modifying
  @Query("UPDATE FamsClass cl SET cl.status = ?1 WHERE cl.code = ?2")
  LearningObjective changeStatus(Boolean status, String code);

  // ? Search by fields filter
  @Query("SELECT cl FROM FamsClass cl " +
      "WHERE (:code IS NULL OR cl.code = :code) AND cl.status = TRUE " +
      "AND (:name IS NULL OR cl.name = :name) " +
      "AND (:duration IS NULL OR cl.duration = :duration) " +
      "AND (:startDate IS NULL OR cl.startDate = :startDate)"+
      "AND (:endDate IS NULL OR cl.endDate = :endDate)" )
  List<FamsClass> searchSortFilter(@Param("code") String code,
      @Param("name") String name,
      @Param("duration") Long duration,
      @Param("startDate") Long startDate,
      @Param("endDate") Long endDate,
      Pageable pageable);

  @Query("SELECT COUNT(cl) FROM FamsClass cl " +
      "WHERE (:code IS NULL OR cl.code = :code) AND cl.status = TRUE " +
      "AND (:name IS NULL OR cl.name = :name) " +
      "AND (:duration IS NULL OR cl.duration = :duration) " +
      "AND (:startDate IS NULL OR cl.startDate = :startDate)"+
      "AND (:endDate IS NULL OR cl.endDate = :endDate)" )
  Long countSearchSortFilter(String code,
      String name,
      Long duration,
      Long startDate,
      Long endDate);


  @Query("SELECT cl FROM FamsClass cl " +
      "WHERE (:code IS NULL OR cl.code = :code) " +
      "AND (:name IS NULL OR cl.name = :name) " +
      "AND (:duration IS NULL OR cl.duration = :duration) " +
      "AND (:startDate IS NULL OR cl.startDate = :startDate)"+
      "AND (:endDate IS NULL OR cl.endDate = :endDate)"+
      "ORDER BY  " +
      "CASE WHEN :sortById ='iDESC' THEN cl.id  END DESC ," +
      "CASE WHEN :sortById ='iASC' THEN cl.id  END ASC ,"+
      "cl.id desc")
  List<FamsClass> searchSortFilterADMIN(@Param("code") String code,
      @Param("name") String name,
      @Param("duration") Long duration,
      @Param("startDate") Long startDate,
      @Param("endDate") Long endDate,
      @Param("sortById") String sortById,
      Pageable pageable);




}
