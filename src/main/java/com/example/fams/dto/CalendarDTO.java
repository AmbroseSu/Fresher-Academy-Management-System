package com.example.fams.dto;
import com.example.fams.entities.enums.WeekDay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarDTO implements Serializable {
    Long id;
    WeekDay weekDay;
    private Long famsClassIds;
}