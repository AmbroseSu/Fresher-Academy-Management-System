package com.example.fams.repository;

import com.example.fams.entities.Material;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, String> {
    Material findByStatusIsTrueAndId(Long id);

    List<Material> findAllByStatusIsTrue(Pageable pageable);

    List<Material> findAllByOrderByIdDesc(Pageable pageable);


    Long countAllByStatusIsTrue();

    Material findById(Long id);
}
