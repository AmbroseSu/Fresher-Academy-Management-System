package com.example.fams.repository;

import com.example.fams.entities.OutputStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutputStandardRepository extends JpaRepository<OutputStandard, String> {

    OutputStandard findByStatusIsTrueAndId(Long id);

    OutputStandard findById(Long id);

    @Query("SELECT os FROM OutputStandard os WHERE os.content.id = :contentId")
    List<OutputStandard> findByContent_Id(Long contentId);
}
