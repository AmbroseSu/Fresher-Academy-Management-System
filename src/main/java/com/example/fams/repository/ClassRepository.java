package com.example.fams.repository;

import com.example.fams.entities.Content;
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
  List<FamsClass> findAllBy(Pageable pageable);
  FamsClass findById(Long id);
  FamsClass findByStatusIsTrueAndId(Long id);
  //FamsClass findByStatusIsTrueAndCode(String code);
  Long countAllByStatusIsTrue();
  @Query("SELECT f FROM FamsClass f WHERE f.startDate <= :inputStartDate AND f.endDate >= :inputStartDate")
  List<FamsClass> findFamsClassWithStartDateInRange(Long inputStartDate);
  @Transactional
  @Modifying
  @Query("UPDATE FamsClass cl SET cl.status = ?1 WHERE cl.code = ?2")
  LearningObjective changeStatus(Boolean status, String code);

  // ? Search by fields filter
  @Query(value = "SELECT * FROM tbl_class cl " +
          "WHERE ((:code IS NULL OR LOWER(cl.code) LIKE LOWER(CONCAT('%', :code,'%')))" +
          "OR (:name IS NULL OR LOWER(cl.name) LIKE LOWER(CONCAT('%', :name,'%'))))" +
          "AND cl.status = TRUE",
          nativeQuery = true)
  List<FamsClass> searchSortFilter(@Param("code") String code,
                                   @Param("name") String name,
                                   Pageable pageable);

  @Query(value = "SELECT COUNT(*) FROM tbl_class cl " +
          "WHERE (:code IS NULL OR LOWER(cl.code) LIKE LOWER(CONCAT('%', :code,'%'))) AND cl.status = TRUE " +
          "OR (:name IS NULL OR LOWER(cl.name) LIKE LOWER(CONCAT('%', :name,'%')))",
          nativeQuery = true)
  Long countSearchSortFilter(String code,
                             String name);

  @Query(value = "SELECT * FROM tbl_class cl " +
          "WHERE (:code IS NULL OR LOWER(cl.code) LIKE LOWER(CONCAT('%', :code,'%')))  " +
          "OR (:name IS NULL OR LOWER(cl.name) LIKE LOWER(CONCAT('%', :name,'%'))) " +
          "ORDER BY " +
          "CASE WHEN :sortById ='iDESC' THEN cl.id END DESC, " +
          "CASE WHEN :sortById ='iASC' THEN cl.id END ASC, " +
          "CASE WHEN :sortById NOT IN ('iDESC', 'iASC') THEN cl.id END DESC",
          nativeQuery = true)
  List<FamsClass> searchSortFilterADMIN(@Param("code") String code,
                                        @Param("name") String name,
                                        @Param("sortById") String sortById,
                                        Pageable pageable);

  @Query("SELECT f FROM FamsClass f WHERE f.startDate <= :dayStartWeek AND f.endDate >= :dayEndWeek")
  List<FamsClass> searchBetweenStartDateAndEndDate(Long dayStartWeek, Long dayEndWeek, Pageable pageable);

  @Query("SELECT COUNT(f) FROM FamsClass f WHERE f.startDate <= :dayStartWeek AND f.endDate >= :dayEndWeek")
  Long countSearchBetweenStartDateAndEndDate(Long dayStartWeek, Long dayEndWeek);
}
