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

    @Query("SELECT m FROM Material m " +
            "WHERE (:name IS NULL OR m.name = :name) AND m.status = TRUE " +
            "AND (:description IS NULL OR m.description = :description)")
    List<Material> searchSortFilter(@Param("name") String name,
                                             @Param("description") String description,
                                             Pageable pageable);

    @Query("SELECT COUNT(m) FROM Material m " +
            "WHERE (:name IS NULL OR m.name = :name) AND m.status = TRUE " +
            "AND (:description IS NULL OR m.description = :description)")
    Long countSearchSortFilter(String name,
                               String description);
    @Query("SELECT m FROM Material m " +
            "WHERE (:name IS NULL OR m.name = :name) " +
            "AND (:description IS NULL OR m.description = :description)"+
            "ORDER BY  " +
            "CASE WHEN :sortById ='iDESC' THEN m.id  END DESC ," +
            "CASE WHEN :sortById ='iASC' THEN m.id  END ASC ,"+
            "m.id desc")
    List<Material> searchSortFilterADMIN(
                                                  @Param("name") String name,
                                                  @Param("description") String description,
                                                  @Param("sortById") String sortById,
                                                  Pageable pageable);

}
