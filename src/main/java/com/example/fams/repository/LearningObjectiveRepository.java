package com.example.fams.repository;

import com.example.fams.entities.LearningObjective;
import com.example.fams.entities.Material;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LearningObjectiveRepository extends JpaRepository<LearningObjective, String> {
    List<LearningObjective> findAllByStatusIsTrue(Pageable pageable);
    List<LearningObjective> findAllBy(Pageable pageable);
    List<LearningObjective> findAllByOrderByIdDesc(Pageable pageable);
    Boolean existsById( Long id);
    LearningObjective findById(Long id);
    LearningObjective findByStatusIsTrueAndId(Long id);
    LearningObjective findByStatusIsTrueAndCode(String code);
    Long countAllByStatusIsTrue();
    @Transactional
    @Modifying
    @Query("UPDATE LearningObjective lo SET lo.status = ?1 WHERE lo.code = ?2")
    LearningObjective changeStatus(Boolean status, String code);

    // ? Search by fields filter
    @Query(value = "SELECT * FROM tbl_learning_objective lo " +
            "WHERE (:code IS NULL OR LOWER(lo.code) LIKE LOWER(CONCAT('%', :code,'%'))) AND lo.status = TRUE " +
            "AND (:name IS NULL OR LOWER(lo.name) LIKE LOWER(CONCAT('%', :name,'%'))) " +
            "AND (:type IS NULL OR lo.type = :type) " +
            "AND (:description IS NULL OR LOWER(lo.description) LIKE LOWER(CONCAT('%', :description,'%')))", nativeQuery = true)
    List<LearningObjective> searchSortFilter(@Param("code") String code,
                                             @Param("name") String name,
                                             @Param("type") Integer type,
                                             @Param("description") String description,
                                             Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM tbl_learning_objective lo " +
            "WHERE (:code IS NULL OR LOWER(lo.code) LIKE LOWER(CONCAT('%', :code,'%'))) AND lo.status = TRUE " +
            "AND (:name IS NULL OR LOWER(lo.name) LIKE LOWER(CONCAT('%', :name,'%'))) " +
            "AND (:type IS NULL OR lo.type = :type) " +
            "AND (:description IS NULL OR LOWER(lo.description) LIKE LOWER(CONCAT('%', :description,'%')))", nativeQuery = true)
    Long countSearchSortFilter(String code,
                               String name,
                               Integer type,
                               String description);


    @Query(value = "SELECT * FROM tbl_learning_objective lo " +
            "WHERE (:code IS NULL OR LOWER(lo.code) LIKE LOWER(CONCAT('%', :code,'%'))) " +
            "AND (:name IS NULL OR LOWER(lo.name) LIKE LOWER(CONCAT('%', :name,'%'))) " +
            "AND (:type IS NULL OR lo.type = :type) " +
            "AND (:description IS NULL OR LOWER(lo.description) LIKE LOWER(CONCAT('%', :description,'%')))"+
            "ORDER BY  " +
            "CASE WHEN :sortById ='iDESC' THEN lo.id  END DESC ," +
            "CASE WHEN :sortById ='iASC' THEN lo.id  END ASC ,"+
            "CASE WHEN :sortById NOT IN ('iDESC', 'iASC') THEN lo.id END DESC", nativeQuery = true)
    List<LearningObjective> searchSortFilterADMIN(@Param("code") String code,
                                                  @Param("name") String name,
                                                  @Param("type") Integer type,
                                                  @Param("description") String description,
                                                  @Param("sortById") String sortById,
                                                  Pageable pageable);



}
