package com.example.fams.services;

import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {

    public String uploadFile(MultipartFile multipartFile);

//    public byte[] downloadFile(String fileName);
//
//    public String delteteFile(String fileName);

}
