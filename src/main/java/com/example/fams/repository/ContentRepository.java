package com.example.fams.repository;

import com.example.fams.entities.Class;
import com.example.fams.entities.Content;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, String> {

  List<Content> findByStatusIsTrue(Pageable pageable);
  Content findById(Long id);
  Page<Content> findAll(Pageable pageable);
  Content findByStatusIsTrueAndId(Long id);

}
