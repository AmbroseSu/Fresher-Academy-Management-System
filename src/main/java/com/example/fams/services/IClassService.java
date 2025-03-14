package com.example.fams.services;

import com.example.fams.dto.CalendarDTO;
import com.example.fams.dto.ClassDTO;
import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.entities.enums.WeekDay;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface IClassService extends IGenericService<ClassDTO>{
  ResponseEntity<?> searchSortFilter(ClassDTO classDTO,
      int page, int limi
  ResponseEntity<?> searchSortFilterADMIN(ClassDTO classDTO,
      String sortById,
      int page, int limit);


  ResponseEntity<?> searchBetweenStartDateAndEndDate(Long dayStartWeek, Long dayEndWeek, int page, int limit);
  ResponseEntity<?> save_withCalendar(ClassDTO classDTO, List<WeekDay> weekDays);

  ResponseEntity<?> changeStatusClassCalendar(Long id);
}
