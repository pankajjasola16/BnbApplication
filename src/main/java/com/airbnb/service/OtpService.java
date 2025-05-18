package com.airbnb.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private final Map<String, String> otpStore = new HashMap<>();
    private final Random random = new Random();

    private final com.airbnb.config.TwilioConfig twilioConfig;

    public OtpService(com.airbnb.config.TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
    }

    public String generateOtp(String phoneNumber) {
        String otp = String.format("%06d", random.nextInt(999999));
        otpStore.put(phoneNumber, otp);

        Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioConfig.getPhoneNumber()),
                "Your OTP is: " + otp
        ).create();

        return otp;
    }

    public boolean validateOtp(String phoneNumber, String otp) {
        String validOtp = otpStore.get(phoneNumber);
        if (validOtp != null && validOtp.equals(otp)) {
            otpStore.remove(phoneNumber); // Invalidate after use
            return true;
        }
        return false;
    }
}
