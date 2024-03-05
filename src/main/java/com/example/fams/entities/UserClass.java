package com.example.fams.entities;

import jakarta.persistence.*;

@Entity
@Table
public class UserClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "classId")
    private FamsClass aClass;

}
