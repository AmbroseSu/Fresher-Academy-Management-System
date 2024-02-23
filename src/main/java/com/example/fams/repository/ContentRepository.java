package com.example.fams.repository;

import com.example.fams.entities.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, String> {

  List<Content> findByStatusIsTrue(Pageable pageable);
  Content findById(Long id);
  Page<Content> findAll(Pageable pageable);
  Content findByStatusIsTrueAndId(Long id);

}
