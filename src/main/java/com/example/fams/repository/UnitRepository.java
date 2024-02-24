package com.example.fams.repository;

import com.example.fams.entities.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByStatusIsTrue(Pageable pageable);
    Page<Unit> findAll(Pageable pageable);
    Optional<Unit> findById(Long id);
    Unit findByStatusIsTrueAndId(Long id);
}
