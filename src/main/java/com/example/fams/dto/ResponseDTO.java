package com.example.fams.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
@Builder
public class ResponseDTO {
    private Object content;
    private String message;
    private List<String> details;
    private int statusCode;
    private MeatadataDTO meatadataDTO;
}
