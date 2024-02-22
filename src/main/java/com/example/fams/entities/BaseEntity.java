package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable  = false)
    private Long id;

    @Column(name = "status")
    private Boolean status = true;

//    @Temporal(TemporalType.DATE) // Specify that only the date part should be stored
    @Column(name = "created_date", updatable = false)
//    @CreationTimestamp
//    @Convert(converter = DateConverter.class) // Use a custom converter
    private Long createdDate;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "modified_date")
    private Long modifiedDate;
}
