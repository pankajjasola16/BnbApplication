package com.airbnb.controller;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PhoneNumberController {

    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    // URL: http://localhost:8080/parsePhoneNumber?numberStr=%2B918850372019

    @GetMapping("/parsePhoneNumber")
    public ResponseEntity<String> parsePhoneNumber(@RequestParam String numberStr) {
        // numberStr --> supply phone number here --> and it will return country code
        try {
            // URL for below: http://localhost:8080/parsePhoneNumber?numberStr=%2B918850372019
//            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(numberStr, "");
            // URL for below: http://localhost:8080/parsePhoneNumber?numberStr=8850372019
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(numberStr, "IN");
            int countryCode = numberProto.getCountryCode();
            return ResponseEntity.ok("Country code: " + countryCode);
        } catch (NumberParseException e) {
            return ResponseEntity.badRequest()
                    .body("NumberParseException was thrown: " + e.toString());
        }
    }
}

// Output  -->  Country code: 91

