package com.airbnb.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// This class helping us to perform to login to --> AWS
// This Config file acts as a medium to Login to AWS and to Login --> we require
// accessKey, secretKey and region --> And then only you can login to AWS

@Configuration
public class AWSS3Config {

    // @Value: - below three line are reading the content from proprties file
    // so to login we are taking the details from properties file

    @Value("${accessKey}")
    private String accessKey;

    @Value("${secretKey}")
    private String secretKey;

    @Value("${region}")
    private String region;

    // And in order to programmatically login we need below methods
    // search in AWS --> developer section --> you will get all these codes but the
    // easy way --> take this code from github

    public AWSCredentials credentials(){
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return credentials;
    }
    
    // whenever you add the third party library --> @Autowired will not work --> untill &
    // unless what is used in below codes (@Bean)
    // because we have used @Autowired:
//             @Autowired
//             private AmazonS3 amazonS3;
    // in BucketService.java --> so use below codes:

    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials());
                // sir has written only above code but line was not visible so i have taken below
                // code from chatGpt

                .withCredentials(new AWSStaticCredentialsProvider(credentials())) // Pass credentials
                .withRegion(region) // Set the region
                .build(); // Build the S3 client
        return s3client;
    }
}

// What Bucket Service does  it takes the  Multipart file,  the bucket name
// But call the service layer from  Controller
// so create --> ImageController.java
