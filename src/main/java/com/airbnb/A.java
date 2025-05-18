package com.airbnb;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class A {
    public static void main(String[] args) {
        //     Approach - 1

//        PasswordEncoder en = new BCryptPasswordEncoder();
//        System.out.println(en.encode("testing"));
        // Output  -->  $2a$10$pgv.DKQTz380gsjj5h0rHO9n1w1Y8yyl18I9qte//oUpns764BaTO

        // PasswordEncoder --> it is an interface and this:
        // BCryptPasswordEncoder() --> is a class

        //     Approach  -  2

//        String encodedPassword = BCrypt.hashpw("testing", BCrypt.gensalt(10));
        // Above line ("testing", BCrypt.gensalt(10)) -->  means encrypt this word 10 times so that
        // encryption will be more strong --> gensalt is a number of rounds of encryption
        // means encrypte text again encrypt
//        System.out.println(encodedPassword);

        //            How to encrypt the pasword by myself and generate that

        // we have to do this for ADMIN

        String password = BCrypt.hashpw("testing", BCrypt.gensalt(10));
        System.out.println(password);

    }
}

// The Encryption to be done we have a particular class --> new BCryptPasswordEncoder (this class is coming from security package (see import)) --> thats why we added Spring security class
// just by creating object of this class --> PasswordEncoder  en --> we can encode the password