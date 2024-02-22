package com.example.fams.repository;

import com.example.fams.entities.TrainingProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, String> {
    List<TrainingProgram> findByStatusIsTrue();
    Optional<TrainingProgram> findByName(String name);
    TrainingProgram findById(Long id);
//    TrainingProgram findByStatusIsTrueAndCode(String code);
    TrainingProgram findByStatusIsTrueAndId(Long id);
    @Transactional
    @Modifying
    @Query("UPDATE TrainingProgram lo SET lo.status = ?1 WHERE lo.id = ?2")
    TrainingProgram changeStatus(Boolean status, String code);
}
