package com.example.fams.dto;

import com.example.fams.entities.enums.DeliveryType;
import com.example.fams.entities.enums.TrainingFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDTO {


  private Long id;

  private Long unitId;

  @NotNull(message = "Content delivery type must not be null")
  private DeliveryType deliveryType;

  @NotNull(message = "Content training format type must not be null")
  private TrainingFormat trainingFormat;

  @NotNull(message = "Content duration must not be null")
  private Long duration;

  private Boolean status;
  private List<Long> outputStandardIds;
  private List<Long> learningObjectiveIds;
  private String createBy;
  private String modifiedBy;
}
