package com.example.fams.dto;


import com.example.fams.entities.enums.ClassStatus;
import com.example.fams.entities.enums.WeekDay;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassCalendarDTO {
  private ClassDTO classDTO;
  private List<WeekDay> weekDays;

}
