package com.example.fams.dto.response;

import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.entities.Content;
import lombok.Data;

import java.util.List;
@Data
public class LearningObjectiveResponse {
    private LearningObjectiveDTO learningObjectiveDTO;
    private List<Content> contents;
}
