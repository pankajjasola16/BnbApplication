package com.airbnb.payload;

import java.util.Date;

public class ErrorDetails {

    private Date timestamp;
    private String message;
    private String details;

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public ErrorDetails(Date timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;

//        This is like a Setters because this is setting up the values
//        so now we will generate Getters:


    }
}


//timestamp  Time when the exception occur (Date & Time)
//message  Post not found
//details  Which is the URL of your project that this Exception occurs
//Then this is easy to analyse (after knowing URL)
//We can add more information as per your necessity

//What does payload do?
//Takes the data from  POSTMAN  and it gives data back to  POSTMAN
