package com.example.fams.entities;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
@Builder
public class ResponseDTO {
    private Object content;
    private String message;
    private List<String> errors;
    private int status;
    private MeatadataDTO meatadataDTO;
}
