package com.example.fams.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity{
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable  = false)
    private Long id;

    @Column(name = "status")
    private Boolean status = true;

    public Boolean isStatus() {
        return status;
    }

//    @Temporal(TemporalType.DATE) // Specify that only the date part should be stored
    @Column(name = "created_date", updatable = false)
    private Long createdDate;

    @Column(name = "create_by")
//    @CreationTimestamp
    private String createBy;

    @Column(name = "modified_by")
    private String modifiedBy;



    @Column(name = "modified_date")
//    @UpdateTimestamp
    private Long modifiedDate;

    @PrePersist
    protected void onCreate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            createBy = "Anonymous";
            modifiedBy = "Anonymous";
        }
        else {
            createBy = authentication.getName();
            modifiedBy = authentication.getName();
        }

        createdDate = new Date().getTime();
        modifiedDate = new Date().getTime();
        status = true;
    }

    @PreUpdate
    protected void onUpdate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        modifiedBy = authentication.getName();
        modifiedDate = new Date().getTime();
    }


    public void markModified() {
        this.onUpdate();
    }
}
