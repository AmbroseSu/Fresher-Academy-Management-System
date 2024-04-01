package com.example.fams.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="tbl_output_standard")
public class OutputStandard extends BaseEntity {
    private Long id;
    private String outputStandardName;

    @ManyToOne
    @JoinColumn(name = "contentId")
    private Content content;
}
