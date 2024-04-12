package com.example.fams.repository;

import com.example.fams.entities.CalendarClass;
import java.util.Calendar;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<CalendarClass, Long> {
  List<CalendarClass> findByFamsClassId(Long id);
}
