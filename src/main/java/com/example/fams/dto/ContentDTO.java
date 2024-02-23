package com.example.fams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDTO {

  private Long id;
  private Integer deliveryType;
  private Long duration;
  private String createBy;
  private Long createdDate;
  private String modifiedBy;
  private Long modifiedDate;
}
