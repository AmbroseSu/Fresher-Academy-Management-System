package com.example.fams.repository;

import com.example.fams.entities.Class;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Class, String> {
  List<Class> findByStatusIsTrue(Pageable pageable);
  Class findById(Long id);
  Page<Class> findAll(Pageable pageable);
  Class findByStatusIsTrueAndCode(String code);
  Class findByStatusIsTrueAndId(Long id);




}
