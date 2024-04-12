package com.example.fams.dto;

import com.example.fams.entities.Syllabus;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingProgramDTO {
    private Long id;

    @NotBlank(message = "Training Program Name must not be blank")
    private String name;

    @NotNull(message = "Start time must not be null")
    private Long startTime;

//    @NotNull(message = "Duration must not be null")
    private Long duration;

    @NotNull(message = "Training status must not be null")
    private Integer training_status;

    private Boolean status;

    private List<Long> syllabusIds;
    private String createBy;
    private String createdDate;
    private String modifiedBy;
}