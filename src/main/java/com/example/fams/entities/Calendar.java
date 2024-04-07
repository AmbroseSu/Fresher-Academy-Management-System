package com.example.fams.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "tbl_calendar")
public class Calendar extends BaseEntity{
    private Boolean mon_morning;
    private Boolean mon_afternoon;
    private Boolean mon_night;
    private Boolean tue_morning;
    private Boolean tue_afternoon;
    private Boolean tue_night;
    private Boolean wed_morning;
    private Boolean wed_afternoon;
    private Boolean wed_night;
    private Boolean thu_morning;
    private Boolean thu_afternoon;
    private Boolean thu_night;
    private Boolean fri_morning;
    private Boolean fri_afternoon;
    private Boolean fri_night;
    private Boolean sat_morning;
    private Boolean sat_afternoon;
    private Boolean sat_night;

    @OneToMany(mappedBy = "calendar")
    private List<ClassCalendar> classCalendars;
}
