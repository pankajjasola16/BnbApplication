package com.airbnb.exception;


// this class will be called from AuthController
public class UserExists extends RuntimeException{

    // below is the constructor
    public UserExists(String msg) {
        super(msg);

    }
}
