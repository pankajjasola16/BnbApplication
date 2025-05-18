package com.airbnb.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;

@Service
public class BucketService {
    @Autowired
    private AmazonS3 amazonS3;

    // this class will take --> Multipart file and Bucket name and:-->
    // multipart file comes from Spring framework
    // below we can see whenever file is submitted --> that file is submitted as a
    // Multipart file to that argument --> and with that file --> I require a Bucket name
    public String uploadFile(MultipartFile file, String bucketName){
        // this below part of the code will upload the file in --> S3 --> and return back
        // the URL -> TO ImageController
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }
        // below give the path of the file
        try {
            // Convert the MultipartFile to a File object and upload it to the S3 bucket using amazonS3.putObject().
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" +file.getOriginalFilename());
            file.transferTo(convFile);
            try {
                amazonS3.putObject(bucketName, convFile.getName(), convFile);
                return amazonS3.getUrl(bucketName, file.getOriginalFilename()).toString();
                // above we are going to get the URL (file URL that we have uploaded)
                // Above Retrieve the file URL from S3 using amazonS3.getUrl().
                // then return
            } catch (AmazonS3Exception s3Exception) {
                return "Unable to upload file :" +s3Exception.getMessage();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to upload file", e);
        }
    }


}
