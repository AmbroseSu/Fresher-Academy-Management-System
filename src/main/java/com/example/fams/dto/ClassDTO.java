package com.example.fams.dto;

import com.example.fams.entities.enums.ClassStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDTO {

  private Long id;

  @NotBlank(message = "FSU must not be blank")
  private String fsu;

  @NotBlank(message = "Class Name must not be blank")
  private String name;

  @NotBlank(message = "Please enter class code")
  private String code;
//  private Long duration;
  private Boolean status;

  @NotNull(message = "Please enter class status")
  private ClassStatus classStatus;

  @NotBlank(message = "Please enter class location")
  private String location;

  @NotNull(message = "Attendee must not be null")
  private String attendee;

  @NotNull(message = "Attendee - actual must not be null")
  private Long attendeeActual;

  @NotNull(message = "Attendee - accepted must not be null")
  private Long attendeeAccepted;

  @NotNull(message = "Attendee - planned must not be null")
  private Long attendeePlanned;

  @NotNull(message = "Class start date must not be null")
  private Long startDate;

  @NotNull(message = "Class end date must not be null")
  private Long endDate;

  @NotNull(message = "Class start time frame must not be null")
  private LocalTime startTimeFrame;

  @NotNull(message = "Class end time frame must not be null")
  private LocalTime endTimeFrame;

//  private List<Long> userIds;
  private List<Long> adminIds;
  private List<Long> trainerIds;
  private Long trainingProgramId;
  private String createBy;
  private String createdDate;
  private String modifiedBy;
}
