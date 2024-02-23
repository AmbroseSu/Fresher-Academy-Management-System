package com.example.fams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDTO {

  private Long id;
  private Integer delivery_type;
  private Long duration;
  private String create_by;
  private Long created_date;
  private String modified_by;
  private Long modified_date;
}
