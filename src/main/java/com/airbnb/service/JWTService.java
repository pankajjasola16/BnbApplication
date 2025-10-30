package com.airbnb.service;

import com.airbnb.entity.AppUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

// @Service --> it will handover the class to Spring Boot because only after annotating
// the class with this annotation it will be loaded into the Spring memory

@Service
public class JWTService {

    // below is a signature
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiry.duration}")
    private int expiryTime;

    // to import Algorithm we need to Add JWT library --> add dependency
    private Algorithm algorithm;


    // @Value("${jwt.issuer}") --> this will automatically go to property file fetch
    // the username --> it will initialize this

    // below method will run automatically because of this --> @PostConstruct

    // without algorithm i cannot generate the Token so use this method:

    private static final String USER_NAME="username";
    // this variable (USER_NAME) --> initially has --> "username"

    @PostConstruct
    public void postContruct() throws UnsupportedEncodingException {

        // @PostConstruct --> this Annotation comes from Hibernate --> this method is
        // automatically going to run because of this Annotation

        // i want to apply here algorithm to generate Token
        // create one variable above
        // here i will work on the header part of the Token because header part
        // consist of Algorithm information so below line will help me to select Algorithm
        // and here apply signature in that --> algorithmKey so that nobody can DCrypt
        // this Algorithm without a secret key
        // do Alt + Enter --> and add unhandled exception
        // and this algorithm comes with secret key and without secret key no one can Dcrypt
        // and secret key is present in the property file --> jwt.algorithm.key=gdhjgsad-ashdjkashdjkh-ashdjshdjhsa

        algorithm = Algorithm.HMAC256(algorithmKey);

//        System.out.println(algorithmKey);
//        System.out.println(issuer);
//        System.out.println(expiryTime);

    }

    public String generateToken(AppUser user){

        Date expirationDate = new Date(System.currentTimeMillis() + expiryTime);
        // formula --> Computer Engineer is UnEmployed
        // below    Token is generated if username and password are correct
        return JWT.create().
                withClaim(USER_NAME,user.getUsername())
                // Above will embed the username into the token for later verification or retrieval.
//                .withExpiresAt(new Date(System.currentTimeMillis()))
                .withClaim("role", user.getRole())         // 👈 add role claim here
                .withClaim("name", user.getName())           //  full name to show who have logged in
                .withClaim("email", user.getEmail())         //  email
                .withExpiresAt(expirationDate)
                .withIssuer(issuer)
                .sign(algorithm);

        // This code is returning a JWT token that includes a custom claim (USER_NAME), an
        // expiration date, an issuer, and is signed with a specified algorithm.

        // now replace "username" with --> USER_NAME
        // withClaim(USER_NAME,user.getUsername()) --> Whatever will be the value of
        // username --> user.getUsername() --> we will put that into --> USER_NAME

                // withClaim("username",user.getUsername()) --> this is payload this
        // information will go to the payload of the Token
    }

    // this method will be called from --> JWTFilter.java
    // formula --> jocky Rocky With BodyBuilder Vikram
    public String getUserName(String token) {
        DecodedJWT decodedjwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        // above line will verify your token in one line and it will decode the Token and will call
        // as decodedJwt
        // require(algorithm) --> in order to decode Token --> your Token is encrypted
        //for encryption which algorithm we have used --> that is present in --> algorithm (HS256)
        // SO DECODE WE WILL USE HS256
        // withIssuer(issuer) --> issuer details are coming from the variable --> issuer (.withIssuer(issuer))
        // so check wheather it is a right issuer who issued the token and the Token coming back
        // is that the same issuer in that

        // now from decoded token we will get the --> username:

//        String username = decodedjwt.getClaim(USER_NAME).asString();

        return decodedjwt.getClaim(USER_NAME).asString();
                // getClaim() here means --> get username
        // decodedjwt.getClaim(USER_NAME); --> again from decoded token (decodedjwt) -->
        // it is putting the value in --> USER_NAME () So variable is being --> reused
        // now convert this value to --> asString() and --> this will return back the --> username

//                Complete process for Token:

//        Http Request come   in doFilter method  then token is given to a method 
//        getUserName()  String username = jwtService.getUserName(tokenVal);
//
//  (in JWTService.java) after taking Token  this method verify the Token  
//        DecodedJWT decodedjwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token); 
//
//        and decode Token  from that token it will get a  username and return back




    }

}

//These lines of code generate a JWT (JSON Web Token) with specific claims and settings:
//
//        JWT.create(): Starts building a new JWT token.
//withClaim(USER_NAME, user.getUsername()): Adds a custom claim (USER_NAME) to the token, embedding the user's username for later use.
//withExpiresAt(expirationDate): Sets the token's expiration time to the provided expirationDate.
//withIssuer(issuer): Specifies the issuer of the token for validation purposes.
//sign(algorithm): Signs the token using the specified cryptographic algorithm (e.g., HMAC256) for security.

