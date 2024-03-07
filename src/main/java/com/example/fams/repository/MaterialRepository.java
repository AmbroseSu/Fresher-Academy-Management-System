package com.example.fams.repository;

import com.example.fams.entities.Material;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, String> {
    Material findByStatusIsTrueAndId(Long id);

    List<Material> findAllByStatusIsTrue(Pageable pageable);

    List<Material> findAllByOrderByIdDesc(Pageable pageable);


    Long countAllByStatusIsTrue();

    Material findById(Long id);

    @Query(value = "SELECT * FROM tbl_material m " +
            "WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name,'%'))) AND m.status = TRUE " +
            "AND (:description IS NULL OR LOWER(m.description) LIKE LOWER(CONCAT('%', :description,'%')))",
            nativeQuery = true)
    List<Material> searchSortFilter(@Param("name") String name,
                                             @Param("description") String description,
                                             Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM tbl_material m " +
            "WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name,'%'))) AND m.status = TRUE " +
            "AND (:description IS NULL OR LOWER(m.description) LIKE LOWER(CONCAT('%', :description,'%')))",
            nativeQuery = true)
    Long countSearchSortFilter(String name,
                               String description);

    @Query(value = "SELECT * FROM tbl_material m " +
            "WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name,'%'))) " +
            "AND (:description IS NULL OR LOWER(m.description) LIKE LOWER(CONCAT('%', :description,'%'))) " +
            "ORDER BY " +
            "CASE WHEN :sortById ='iDESC' THEN m.id END DESC, " +
            "CASE WHEN :sortById ='iASC' THEN m.id END ASC, " +
            "CASE WHEN :sortById NOT IN ('iDESC', 'iASC') THEN m.id END DESC",
            nativeQuery = true)
    List<Material> searchSortFilterADMIN(
                                                  @Param("name") String name,
                                                  @Param("description") String description,
                                                  @Param("sortById") String sortById,
                                                  Pageable pageable);

}
