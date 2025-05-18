package com.airbnb.service;

import com.airbnb.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

                      // Without Custom Annotation

// we have created Custom Annotation in Validation package so no need of this class

// this class we are calling from boking controller:

//  validateBookingDatesService.validateBookingDates(booking.getCheckInDate(), booking.getCheckOutDate());

// this is for validating the date --> whether it is previous or Past date

// we can create below if condition just below the --> createBooking method --> in Booking Controller

@Service
public class ValidateBookingDatesService {

    public void validateBookingDates(LocalDate checkInDate, LocalDate checkOutDate) {
        LocalDate today = LocalDate.now();

        if (checkInDate.isBefore(today)) {
            throw new ResourceNotFoundException("Check-in date must be a future date.");
        }

        if (checkOutDate.isBefore(checkInDate)) {
            throw new ResourceNotFoundException("Check-out date must be after the check-in date.");
        }
    }
}

