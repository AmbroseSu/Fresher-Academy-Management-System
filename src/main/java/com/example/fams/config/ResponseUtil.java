package com.example.fams.config;

import com.example.fams.entities.MeatadataDTO;
import com.example.fams.entities.ResponseDTO;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.awt.print.Pageable;
import java.util.Collections;

@UtilityClass
public class ResponseUtil {
    public static ResponseEntity<ResponseDTO> getObject(Object result, HttpStatus status, String response){
        return new ResponseEntity<>(
                ResponseDTO.builder()
                        .status(status.value())
                        .errors(ExceptionUtils.getResponseString(response))
                        .content(result)
                        .build()
                , status
        );
    }

    public static ResponseEntity<?> getCollection(Object result, HttpStatus status, String response
    ,int page, int limit, int count) {
        return new ResponseEntity<>(
                ResponseDTO.builder()
                        .status(status.value())
                        .errors(ExceptionUtils.getResponseString(response))
                        .content(result)
                        .meatadataDTO(getMeatadata(page,limit,count))
                        .build()
                , status
        );
    }

    public MeatadataDTO getMeatadata(int page, int limit, int count){
        MeatadataDTO result = new MeatadataDTO();
        result.setPage(page);
        result.setTotal(((int) Math.ceil((double) count/limit)));
        result.setLimit(limit);
        if(limit <= count) {
            result.setHasNextPage(false);
            result.setHasPrevPage(false);
        }else {
            if(result.getPage() >1 && result.getPage() < result.getTotal()) {
                result.setHasNextPage(true);
                result.setHasPrevPage(true);
            }
            if(result.getPage() == 1)   {
                result.setHasNextPage(false);
                result.setHasPrevPage(true);
            }
            if(result.getPage() == result.getTotal()) {
                result.setHasNextPage(false);
                result.setHasPrevPage(true);
            }
        }
        return result;

    }

    public static ResponseEntity<?> error(String error, String message,HttpStatus status) {
        return new ResponseEntity<> (
                ResponseDTO.builder()
                        .status(status.value())
                        .message(message)
                        .errors(ExceptionUtils.getError(error))
                        .build()
                ,status
        );
    }


}
