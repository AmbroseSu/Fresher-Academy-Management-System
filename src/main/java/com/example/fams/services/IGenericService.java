package com.example.fams.services;

import com.example.fams.dto.ClassDTO;
import org.springframework.http.ResponseEntity;

public interface IGenericService<T> {
    ResponseEntity<?> findById(Long id);

    ResponseEntity<?> findAllByStatusTrue(int page, int limit);
    ResponseEntity<?> findAll(int page, int limit);

    ResponseEntity<?> save(T t);

    ResponseEntity<?> changeStatus(Long id);

}
