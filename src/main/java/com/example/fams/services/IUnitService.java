package com.example.fams.services;

import com.example.fams.dto.UnitDTO;
import org.springframework.http.ResponseEntity;

public interface IUnitService  extends IGenericService<UnitDTO>{
    ResponseEntity<?> searchSortFilter(UnitDTO unitDTO,
                                       int page, int limit);

    ResponseEntity<?> searchSortFilterADMIN(UnitDTO unitDTO,
                                            String sortById,
                                            int page, int limit);


}
