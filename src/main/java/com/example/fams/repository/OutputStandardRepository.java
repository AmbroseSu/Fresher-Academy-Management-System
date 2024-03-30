package com.example.fams.repository;

import com.example.fams.entities.OutputStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutputStandardRepository extends JpaRepository<OutputStandard, String> {

    OutputStandard findByStatusIsTrueAndId(Long id);

    OutputStandard findById(Long id);
}
