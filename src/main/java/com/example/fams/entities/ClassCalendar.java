//package com.example.fams.entities;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//@Data
//@Entity
//@Table(name = "tbl_class_calendar")
//public class ClassCalendar {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "famsClassId")
//    private FamsClass famsClass;
//
//    @ManyToOne
//    @JoinColumn(name = "calendarId")
//    private Calendar calendar;
//
//}
