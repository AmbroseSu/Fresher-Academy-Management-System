package com.example.fams.repository;

import com.example.fams.entities.Content;
import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.Unit;
import com.example.fams.entities.enums.DeliveryType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, String> {

  List<Content> findAllByStatusIsTrue(Pageable pageable);
  List<Content> findAllByOrderByIdDesc(Pageable pageable);
  List<Content> findAllBy(Pageable pageable);
  Content findById(Long id);
  Content findByStatusIsTrueAndId(Long id);
  //Content findByStatusIsTrueAndCode(String code);
  Long countAllByStatusIsTrue();
  List<Content> findAllByUnitId(Long unitId);

  @Transactional
  @Modifying
  @Query("UPDATE Content co SET co.status = ?1 WHERE co.id = ?2")
  LearningObjective changeStatus(Boolean status, Long id);

  // ? Search by fields filter
  @Query("SELECT co FROM Content co " +
      "WHERE co.status = TRUE " +
      "AND (:deliveryType IS NULL OR co.deliveryType = :deliveryType) " +
      "AND (:duration IS NULL OR co.duration = :duration) ")
  List<Content> searchSortFilter(
      @Param("deliveryType") DeliveryType deliveryType,
      @Param("duration") Long duration,
      Pageable pageable);

  @Query("SELECT COUNT(co) FROM Content co " +
      "WHERE co.status = TRUE " +
      "AND (:deliveryType IS NULL OR co.deliveryType = :deliveryType) " +
      "AND (:duration IS NULL OR co.duration = :duration) " )
  Long countSearchSortFilter(
      DeliveryType deliveryType,
      Long duration);


  @Query("SELECT co FROM Content co " +
      "WHERE (:deliveryType IS NULL OR co.deliveryType = :deliveryType) " +
      "AND (:duration IS NULL OR co.duration = :duration) "+
      "ORDER BY  " +
      "CASE WHEN :sortById ='iDESC' THEN co.id  END DESC ," +
      "CASE WHEN :sortById ='iASC' THEN co.id  END ASC ,"+
      "co.id desc")
  List<Content> searchSortFilterADMIN(@Param("deliveryType") DeliveryType deliveryType,
      @Param("duration") Long duration,
      @Param("sortById") String sortById,
      Pageable pageable);

  @Query("SELECT c.unit FROM Content c WHERE c.unit.id = :unitId")
    Unit findUnitByUnitId(@Param("unitId") Long unitId);
}
