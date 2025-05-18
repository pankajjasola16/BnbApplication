package com.airbnb.controller;

import com.airbnb.entity.AppUser;
import com.airbnb.payload.JWTToken;
import com.airbnb.payload.OtpRequest;
import com.airbnb.payload.OtpVerificationRequest;
import com.airbnb.repository.AppUserRepository;
import com.airbnb.service.JWTService;
import com.airbnb.service.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/otp")
public class OtpAuthController {

    private final OtpService otpService;
    private final AppUserRepository appUserRepository;
    private final JWTService jwtService;

    public OtpAuthController(OtpService otpService, AppUserRepository appUserRepository, JWTService jwtService) {
        this.otpService = otpService;
        this.appUserRepository = appUserRepository;
        this.jwtService = jwtService;
    }

    // http://localhost:8080/api/v1/otp/send
    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest otpRequest) {
        otpService.generateOtp(otpRequest.getPhoneNumber());
        return ResponseEntity.ok("OTP sent to " + otpRequest.getPhoneNumber());
    }
    // http://localhost:8080/api/v1/otp/verify
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        boolean isValid = otpService.validateOtp(request.getPhoneNumber(), request.getOtp());

        if (isValid) {
            Optional<AppUser> opUser = appUserRepository.findByPhoneNumber(request.getPhoneNumber());

            if (opUser.isPresent()) {

                AppUser appUser = opUser.get();

                String token = jwtService.generateToken(appUser);

                JWTToken jwtToken = new JWTToken();
                jwtToken.setTokenType("JWT");
                jwtToken.setToken(token);

//                JWTToken jwtToken = new JWTToken("JWT", token);
                return ResponseEntity.ok(jwtToken);
            } else {
                return ResponseEntity.status(404).body("User with this phone number not found");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
    }
}
