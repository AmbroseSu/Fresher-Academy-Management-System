package com.example.fams.repository;

import com.example.fams.entities.CalendarClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<CalendarClass, Long> {

}
