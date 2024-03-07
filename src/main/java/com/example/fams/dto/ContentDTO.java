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
  @NotNull
  private Integer deliveryType;
  @NotNull
  private Long duration;
  @NotNull
  private Boolean status;
  private List<Long> learningObjectiveIds;
}
