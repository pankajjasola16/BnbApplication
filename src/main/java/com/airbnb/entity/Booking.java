package com.airbnb.entity;

import com.airbnb.validation.FutureOrToday;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "no_of_guests")
    private Integer noOfGuests;

    @Pattern(
            regexp = "^(?!\\d+$)[A-Za-z ]+$",
            message = "Guest name must contain only letters and cannot be only numbers."
    )
    @Column(name = "guest_name", nullable = false)
    private String guestName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits.")
    @Column(name = "mobile", nullable = false, length = 10)
    private String mobile;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "type_of_room", nullable = false)
    private String typeOfRoom;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @Column(name = "total_price", nullable = false)
    private Float total_price;

    @Column(name = "total_nights", nullable = false)
    private Integer totalNights;

    @Column(name = "status", nullable = false)
    private String status; // ACTIVE, CANCELED, COMPLETED

    // checkIn Date -->minus 1 room count:

                        //   Custom Annotation

    // we have created --> validation package --> one annotation interface called as --> FutureOrToday
    // and one class --> FutureOrTodayValidator --> and throw these two class we have created -->
    // one custom Annotation called as --> @FutureOrToday

    @FutureOrToday(message = "Check-in date must be today or in the future.")
    @NotNull(message = "Check-in date cannot be null.")
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;


    @FutureOrToday(message = "Check-out date must be today or in the future.")
    @NotNull(message = "Check-out date cannot be null.")
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

                            //    payment gateway

//    @Transient // This field is only used temporarily during booking, not stored in DB
//    private String stripeToken;
//
//    public String getStripeToken() {
//        return stripeToken;
//    }
//
//    public void setStripeToken(String stripeToken) {
//        this.stripeToken = stripeToken;
//    }


    //    @Column(name = "check_in_date", nullable = false)
//    private LocalDate checkInDate;
//
//    // checkin to Checkout date --> how many days are there --> so build a logic
//
//    @Column(name = "check_out_date", nullable = false)
//    private LocalDate checkOutDate;

    // without total nights we cannot calculate the price and there must be booking date
    // we should always update the room date wise

         // if we do not add this:

    //    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(

    // in GlobalExceptionHandler.java:

    // we will get the below message in POSTMAN

//    {
//        "type": "about:blank",
//            "title": "Bad Request",
//            "status": 400,
//            "detail": "Invalid request content.",
//            "instance": "/api/v1/booking/createBooking"
//    }

    // BUT IF I ADD code in GlobalExceptionHandler.java:

    // I will get custom message:

//    {
//        "checkOutDate": "Check-out date must be today or in the future.",
//            "checkInDate": "Check-in date must be today or in the future."
//    }

}
