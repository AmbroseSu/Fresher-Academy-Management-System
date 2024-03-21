package com.example.fams.services;
import com.example.fams.dto.MaterialDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IMaterialService extends IGenericService<MaterialDTO>{
    ResponseEntity<?> searchSortFilter(MaterialDTO materialDTO,
                                       int page, int limit);

    ResponseEntity<?> searchSortFilterADMIN(MaterialDTO materialDTO,
                                            String sortById,
                                            int page, int limit);

    ResponseEntity<?> upload(MultipartFile multipartFile,
                             Long materialId);
}
