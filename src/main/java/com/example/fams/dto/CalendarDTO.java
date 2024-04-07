package com.example.fams.dto;
import com.example.fams.entities.FamsClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarDTO implements Serializable {
    Long id;
    Boolean status;
    Boolean mon_morning;
    Boolean mon_afternoon;
    Boolean mon_night;
    Boolean tue_morning;
    Boolean tue_afternoon;
    Boolean tue_night;
    Boolean wed_morning;
    Boolean wed_afternoon;
    Boolean wed_night;
    Boolean thu_morning;
    Boolean thu_afternoon;
    Boolean thu_night;
    Boolean fri_morning;
    Boolean fri_afternoon;
    Boolean fri_night;
    Boolean sat_morning;
    Boolean sat_afternoon;
    Boolean sat_night;
    private List<Long> famsClassIds;
}