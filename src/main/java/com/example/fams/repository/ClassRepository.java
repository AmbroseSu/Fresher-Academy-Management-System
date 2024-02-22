package com.example.fams.repository;

import com.example.fams.entities.Class;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Class, String> {
  List<Class> findByStatusIsTrue();
  Class findById(Long id);
  Class findByStatusIsTrueAndCode(String code);
  Class findByStatusIsTrueAndId(Long id);




}
