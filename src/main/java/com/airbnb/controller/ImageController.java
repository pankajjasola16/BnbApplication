package com.airbnb.controller;

import com.airbnb.entity.AppUser;
import com.airbnb.entity.Image;
import com.airbnb.entity.Property;
import com.airbnb.exception.ResourceNotFoundException;
import com.airbnb.payload.ImageDTO;
import com.airbnb.repository.ImageRepository;
import com.airbnb.repository.PropertyRepository;
import com.airbnb.service.BucketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private ImageRepository imageRepository;
    private PropertyRepository propertyRepository;
    private BucketService bucketService;


    public ImageController(ImageRepository imageRepository, PropertyRepository propertyRepository, BucketService bucketService) {
        this.imageRepository = imageRepository;
        this.propertyRepository = propertyRepository;
        this.bucketService = bucketService;
    }

    // fill below path --> (path ="/upload/file/{bucketName}/property/{propertyId}
    // so below just supply --> Bucket name , propertyId and file (MultipartFile file)

    // http://localhost:8080/api/v1/images/upload/file/gauranga-2024/property/2

    @PostMapping(path ="/upload/file/{bucketName}/property/{propertyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam MultipartFile file,
                                        @PathVariable String bucketName,
                                        @PathVariable long propertyId,
                                        @AuthenticationPrincipal AppUser user) {
        // MultipartFile is an interface in Spring that represents an uploaded file in a multipart request.
        // It is commonly used to handle file uploads, such as images, documents, or videos.
        // Used as a method parameter in a @RestController to accept uploaded files
        // Spring automatically binds the uploaded file to the MultipartFile object.
        // below we are calling the method which is in --> BucketService.java and from  there
        // we will get the URL
        // below we are supplying Multipart file and bucket name
        String imageUrl = bucketService.uploadFile(file, bucketName);
//        Property property = propertyRepository.findById(propertyId).get();
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                ()-> new ResourceNotFoundException("this property does not exist")
        );

        // below Image entity will set the URL and set the property object --> which is foreign key
        Image img = new Image();
        img.setUrl(imageUrl);
        img.setProperty(property);

        Image savedImage = imageRepository.save(img);

        System.out.println(savedImage.getId());
        System.out.println(savedImage.getUrl());

        // Convert to DTO
        ImageDTO imageDTO = new ImageDTO(savedImage.getId(), savedImage.getUrl());

        return new ResponseEntity<>(imageDTO, HttpStatus.OK);

//        return new ResponseEntity<>(savedImage, HttpStatus.OK);

    }
}

//                        Common Methods in MultipartFile

//Method	                            Description

//getOriginalFilename()	                Returns the original file name
//getSize()                         	Returns the file size in bytes
//getBytes()	                        Returns the file content as a byte array
//getInputStream()	                    Returns an InputStream to read the file content
//transferTo(File dest)	                Saves the file to a given location

