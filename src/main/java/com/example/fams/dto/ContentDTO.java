package com.example.fams.dto;

import com.example.fams.entities.Unit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDTO {


  private Long id;

  private Long unitId;

  @NotNull(message = "Content delivery type must not be null")
  private Integer deliveryType;

  @NotNull(message = "Content duration must not be null")
  private Long duration;

  private Boolean status;
  private List<Long> learningObjectiveIds;
}
