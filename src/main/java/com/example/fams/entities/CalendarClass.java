package com.example.fams.entities;
import com.example.fams.entities.enums.WeekDay;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_calendar")
public class CalendarClass extends BaseEntity{

    private List<WeekDay> weekDays;

    @ManyToOne
    @JoinColumn(name = "famsClassId")
    private FamsClass famsClass;
}
