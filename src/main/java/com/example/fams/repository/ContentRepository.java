package com.example.fams.repository;

import com.example.fams.entities.Content;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, String> {

  List<Content> findByStatusIsTrue();
  Content findById(Long id);
  Content findByStatusIsTrueAndId(Long id);

}
