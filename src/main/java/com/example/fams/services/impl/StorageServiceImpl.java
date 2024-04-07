package com.example.fams.services.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.fams.services.IStorageService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@Service
@NoArgsConstructor
@AllArgsConstructor
public class StorageServiceImpl implements IStorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;


    public String uploadFile(MultipartFile multipartFile) {
        File file = convertMultipartfileToFile(multipartFile);
        String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
        file.delete();
        return s3Client.getUrl(bucketName, fileName).toString();
    }

//    public byte[] downloadFile(String fileName) {
//        S3Object s3Object = s3Client.getObject(bucketName, fileName);
//        S3ObjectInputStream inputStream = s3Object.getObjectContent();
//        try {
//            byte[] content = IOUtils.toByteArray(inputStream);
//            return content;
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//        return null;
//    }
//
//    public String delteteFile(String fileName) {
//        s3Client.deleteObject(bucketName, fileName);
//        return "Removed file name: " + fileName;
//    }

    private File convertMultipartfileToFile(MultipartFile multipartFile) {
        File convertedFile = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return convertedFile;
    }
}
