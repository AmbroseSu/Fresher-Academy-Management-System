package com.example.fams.repository;

import com.example.fams.entities.Syllabus;
import com.example.fams.entities.TrainingProgram;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyllabusRepository extends JpaRepository<Syllabus, Long > {
    List<Syllabus> findAllByStatusIsTrue(Pageable pageable);
    List<Syllabus> findAllByOrderByIdDesc(Pageable pageable);
    Syllabus findOneById(Long id);
    Syllabus findByStatusIsTrueAndId(Long id);
    Syllabus findByStatusIsTrueAndName(String name);
    Long countAllByStatusIsTrue();
}
