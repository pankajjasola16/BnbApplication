package com.airbnb.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {
    private Long bookingId;
    private String guestName;
    private String mobile;
    private String email;
    private float totalPrice;
    private int noOfGuests;
    private int totalNights;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String message;


    // Getters and Setters (or use Lombok's @Data for brevity)
}

