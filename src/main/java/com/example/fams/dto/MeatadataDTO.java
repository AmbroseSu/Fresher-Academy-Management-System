package com.example.fams.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeatadataDTO  {
    private boolean hasNextPage;
    private boolean hasPrevPage;
    private int limit;
    private int total;
    private int page;
}
