package com.airbnb.controller;

import com.airbnb.entity.*;
import com.airbnb.payload.BookingResponseDTO;
//import com.airbnb.payload.PaymentRequest;
import com.airbnb.payload.MyBookingDto;
import com.airbnb.repository.*;
import com.airbnb.service.BookingService;
import com.airbnb.service.PDFService;
//import com.airbnb.service.PaymentService;
import com.airbnb.service.SmsService;
import com.airbnb.service.ValidateBookingDatesService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.airbnb.DateUtil.getDatesBetween;



@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private RoomRepository roomRepository;
    private PropertyRepository propertyRepository;
    private BookingRepository bookingRepository;
    private PDFService pdfService;
    private SmsService smsService;
    private ValidateBookingDatesService validateBookingDatesService;
    private BookingService bookingService;

//    @Autowired
//    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public BookingController(RoomRepository roomRepository, PropertyRepository propertyRepository, BookingRepository bookingRepository, PDFService pdfService, SmsService smsService, ValidateBookingDatesService validateBookingDatesService, PaymentRepository paymentRepository, AppUserRepository appUserRepository) {
        this.roomRepository = roomRepository;
        this.propertyRepository = propertyRepository;
        this.bookingRepository = bookingRepository;
        this.pdfService = pdfService;
        this.smsService = smsService;
        this.validateBookingDatesService = validateBookingDatesService;
        this.paymentRepository = paymentRepository;
        this.appUserRepository = appUserRepository;
    }
    // before I do the booking --> search whether the room is available or not upon supplying -->
    // propertyId and roomType

                      //Step 1: Check availability + Create Stripe PaymentIntent

    // http://localhost:8080/api/v1/booking/checkAvailabilityAndCreatePayment?propertyId=123&roomType=DELUXE
    @PostMapping("/checkAvailabilityAndCreatePayment")
    public ResponseEntity<Map<String, Object>> checkAvailabilityAndCreatePayment(
            @RequestParam long propertyId,
            @RequestParam String roomType,
            @RequestBody @Valid Booking booking
    ) throws StripeException {

        Map<String, Object> response = new HashMap<>();

        // Validation
        if (booking.getCheckOutDate().isBefore(booking.getCheckInDate())) {
            response.put("success", false);
            response.put("message", "Checkout date cannot be before Check-in date.");
            return ResponseEntity.badRequest().body(response);
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        // Check availability across all dates
        List<LocalDate> datesBetween = getDatesBetween(booking.getCheckInDate(), booking.getCheckOutDate());
        List<Room> rooms = new ArrayList<>();

        for (LocalDate date : datesBetween) {
            Room room = roomRepository.findRoomByPropertyIdTypeAndDateInRange(propertyId, roomType, date);
            if (room == null || room.getCount() == 0) {
                response.put("success", false);
                response.put("message", "No rooms available on: " + date);
                return ResponseEntity.badRequest().body(response);
            }
            rooms.add(room);
        }

        // Calculate total price
        float total = 0;
        for (Room room : rooms) {
            total += room.getPrice();
        }

        //  Create Stripe PaymentIntent
        Map<String, Object> params = new HashMap<>();
        params.put("amount", (long) (total * 100)); // Stripe expects smallest currency unit
        params.put("currency", "aed"); // use "usd" or "inr" if needed
        params.put("description", "Booking payment for propertyId: " + propertyId);

        PaymentIntent paymentIntent = PaymentIntent.create(params);


//        // Get logged-in user from SecurityContext
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName(); // could be email or username
//
////  Fetch the user entity from DB
//        AppUser user = appUserRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
////  Save payment details to database
//        Payment payment = new Payment();
//        payment.setBookingId(booking.getId());             // booking id
//        payment.setAmount(total);                          // total price (AED)
//        payment.setCurrency("AED");                        // currency
//        payment.setStatus(paymentIntent.getStatus());      // Stripe status
//        payment.setPaymentIntentId(paymentIntent.getId()); // Stripe PI id
//        payment.setReceiptEmail(user.getEmail());          // logged-in user email

        // Save payment details to database using builder
//        Payment payment = Payment.builder()
//                .bookingId(booking.getId())                 // tie to your booking (or temp ID if needed)
//                .amount(total)                              // store total as float (e.g., 999.0)
//                .currency("AED")                            // match your PaymentIntent currency
//                .status(paymentIntent.getStatus())          // Stripe PI status (requires_payment_method, etc.)
//                .paymentIntentId(paymentIntent.getId())     // Stripe PI id
//                .receiptEmail(booking.getUser().getEmail()) // user email
//                .build();

//        paymentRepository.save(payment);
// Optionally, associate with Booking or User if needed:
// payment.setBooking(booking); or payment.setUser(user);

//        paymentRepository.save(payment);

//  Success response
        response.put("success", true);
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("amount", total);

        return ResponseEntity.ok(response);

    }


    //      Step 2: Create booking AFTER payment success


    // URL --> http://localhost:8080/api/v1/booking/createBooking?propertyId=1&roomType=executive
    // URL --> http://localhost:8080/api/v1/booking/createBooking?propertyId=1&roomType=delux
    @PostMapping("/createBooking")
    public ResponseEntity<?> createBooking(
            @RequestParam long propertyId,
            @RequestParam String roomType,
            @RequestBody @Valid Booking booking,
            @AuthenticationPrincipal AppUser user
    ) {

        // @Valid --> it will perform validation on the Booking object in the @RequestBody
        // before the controller method executes --> see details at last
        // When @Valid is used on a parameter like @RequestBody Booking booking, the validation
        // framework inspects the Booking object to ensure all the constraints specified in its class are satisfied.

        if (booking.getCheckOutDate().isBefore(booking.getCheckInDate())) {
            return new ResponseEntity<>("Checkout date cannot be before Check-in date.",HttpStatus.BAD_REQUEST);
        }

        // What if both dates are the same? check for that.

        if (booking.getCheckInDate().isAfter(booking.getCheckOutDate())) {
            return new ResponseEntity<>("Checkout date must be after Check-in date.",HttpStatus.BAD_REQUEST);
        }

//        if (booking.getCheckInDate().isBefore(LocalDate.now())) {
//            return new ResponseEntity<>("Booking cannot be done on past date.", HttpStatus.BAD_REQUEST);
//        }
        // below Check Future or Past Date --> using Custom Validation logic without annotation

//    validateBookingDatesService.validateBookingDates(booking.getCheckInDate(), booking.getCheckOutDate());

        // Save the booking to the database

        // checkin and checkout date comes from --> booking
        // below code will give me property details according to Property Id
        // exception handling for property not found --> see at last
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        // below it returns date as a List --> below it is getting all the dates and every date --> i need
        // to check the --> room availability
        // if we give "checkInDate": "2024-11-20"  AND "checkOutDate": "2024-11-23"   THEN
        //  Below object --> datesBetween --> will have --> 4 dates --> 2024-11-20, 2024-11-21
        // 2024-11-22, 2024-11-23   AND one by one these dates will go into --> For loop

        List<LocalDate> datesBetween = getDatesBetween(booking.getCheckInDate(), booking.getCheckOutDate());

        int totalNights = datesBetween.size();  // Now correct (excludes checkout date)
        booking.setTotalNights(totalNights);


        // below whenever we get the room --> I would like to add that to a List --> because
        // if the rooms are available --> all the 4 rooms data (object) --> should be stored
        // in a List --> and then from that object one by one --> i have to remove the count
        // column wise from there --> so in this case i will build another List:
        // then take that --> rooms object after the if condition
        List<Room> rooms = new ArrayList<>();

        for (LocalDate date : datesBetween) {

            // below there will not be a List of rooms --> here combination of propertyId and
            // type of room and date will give me only one record --> so it will return only one room object

            // step: 1 --> check the room
//            Room room = roomRepository.findByPropertyIdAndTypeAndDate(propertyId, roomType, date);
            Room room = roomRepository.findRoomByPropertyIdTypeAndDateInRange(propertyId, roomType, date);
            // Step: 2 --> if rooms are available
            // if condition is checking room availability

            // Below if we give the date which is not available in database for property booking:

            if (room == null) {

                return new ResponseEntity<>("Room not found for the given property and room type on date: " +date,HttpStatus.BAD_REQUEST);
            }

            // below if rooms are not available or room count should not be negative

            if (room.getCount() == 0) {
                // Below if count is zero --> it will remove the List data and let us not keep the list data
                // populated --> so by doing this it will remove all the rooms object --> means
                // in the rooms (rooms.add(room)) object --> remove all data --> so that list is not
                // populated with the data
//            rooms.clear();
                return new ResponseEntity<>("No rooms available",HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // when if condition is false --> I would like to add here room object in the List
            // so I am creating a List --> which will have rooms availability details in it
            // if there are 4 rooms available --> those 4 objects of the room will be present in it
            // this will have all rooms details
            rooms.add(room);
        }

        // Booking:
        // we have different price in different date in room table
        // keep total=0 --> outside for loop --> otherwise total will become zero every time
        float total = 0;
        for (Room room : rooms) {
            total = total + room.getPrice();
            // total --> this will take 1st day room price then 2nd day room price and then
            // 3rd day room price --> so total price from checkin to checkout day
            // logic will vary based on implementation(business logic) --> here I am assuming
            // everyday the price is different --> But sometimes they keep the same price -->But
            // sometimes price may vary depending on the season
            // if prices are same --> then better you create another table --> and do the
            // mapping between them
        }
        // it will print total price of all three days
//        System.out.println(total);
        booking.setTotal_price(total);

        // Step: Payment with Stripe
//    PaymentRequest paymentRequest = new PaymentRequest();
//    paymentRequest.setAmount((long) (total * 100)); // Stripe uses cents
//    paymentRequest.setCurrency("usd");
//    paymentRequest.setDescription("Payment for booking propertyId: " + propertyId);
//    paymentRequest.setStripeToken(booking.getStripeToken()); // Provided from frontend
//
//    try {
//        paymentService.charge(paymentRequest);
//    } catch (StripeException e) {
//        Map<String, String> error = new HashMap<>();
//        error.put("error", "Payment failed: " + e.getMessage());
//        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(error);
//    }

        // set the property id in booking table:
        booking.setProperty(property);
        // Appuser has to be a JWT Token --> which will populate the Appuser:
        booking.setAppUser(user);
        booking.setTypeOfRoom(roomType);
        booking.setStatus("ACTIVE");
        Booking savedBooking = bookingRepository.save(booking);

        // Step: 3 --> update the room
        // if rooms are available --> then do the booking --> but every time you do the booking
        // update the room --> so first update:
        // below for loop --> will decrease the room count on every date --> means from Checkin
        // date to Checkout date, if checkin --> 23  AND checkout --> 25  THEN it will decrease
        // the room count of --> 23, 24  AND 25 all the dates
        // so below we have updated the room for List of dates

        // below i was using when i was writing the every dates on different column for same hotel , so from each date it was
        // reducing one count , but now i am using start date and end date in the same row for one hotel so reduce the room
        // count only one, so i have used another approch

//        if (savedBooking != null) {
//            for (Room room : rooms) {
//                int availableRooms = room.getCount();
//                room.setCount(availableRooms - 1);
//                roomRepository.save(room);
//            }
//        }

        // Reduce count only ONCE — for the first room (e.g., first day)
        if (savedBooking != null && !rooms.isEmpty()) {
            Room roomToUpdate = rooms.get(0);  // Get the first date's room
            int availableRooms = roomToUpdate.getCount();
            roomToUpdate.setCount(availableRooms - 1);  // Reduce by 1 only
            roomRepository.save(roomToUpdate);
        }


        // Generate PDF Document --> trigger PDF Service creation from here
        // and then send the attachment to email --> pankajjasola16@gmail.com --> in
        pdfService.generatePdf(savedBooking);

        // Send SMS Confirmation

        // for below use this --> "mobile": "+918850372019"
//    smsService.sendSms(booking.getMobile(),"Your booking is confirmed, your booking id is: "+booking.getId());
        smsService.sendSms("+91" + booking.getMobile(), "Your booking is confirmed, your booking id is: " + booking.getId());
        // above i am using --> +91 --> so need to write +91 in JSON --> But this is not the right way
        // "mobile": "8850372019"

        // Send WhatsApp Confirmation

        smsService.sendWhatsApp(booking.getMobile(), "Your booking is confirmed, your booking id is: " + booking.getId());

        // Prepare a concise response of booking details back to the POSTMAN

    BookingResponseDTO responseDTO = new BookingResponseDTO(
            savedBooking.getId(),
            booking.getGuestName(),
            booking.getMobile(),
            booking.getEmail(),
            booking.getTotal_price(),
            booking.getNoOfGuests(),
            booking.getTotalNights(),
            booking.getCheckInDate(),
            booking.getCheckOutDate(),
            "Your booking is successfully created."
    );

    return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        // OR Simple Response

//        Map<String, Object> response = new HashMap<>();
////        response.put("bookingId", savedBooking.getId());
//        response.put("id", savedBooking.getId());  //  Correct field name
//        response.put("guestName", booking.getGuestName());
//        response.put("mobile", booking.getMobile());
//        response.put("email", booking.getEmail());
//        response.put("total_price", booking.getTotal_price());
//        response.put("totalNights", booking.getTotalNights());
//        response.put("noOfGuests", booking.getNoOfGuests());
//        response.put("checkInDate", booking.getCheckInDate());
//        response.put("checkOutDate", booking.getCheckOutDate());
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Helper method to get all dates between two dates inclusive
    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;

        // currentDate.isAfter(endDate) --> means current date is  20 and end date is 23
        // so here it is asking --> is 20 after 23 --> no so condition is false but if we write:
        // !currentDate.isAfter(endDate --> true --> because we are using --> ! --> not
//        while (!currentDate.isAfter(endDate)) {
        // Use isBefore instead of !isAfter to exclude checkout day
        while (currentDate.isBefore(endDate)) {
            dates.add(currentDate);

            currentDate = currentDate.plusDays(1);
        }

        return dates;
    }



    // ===================== ADDED BOOKING DETAILS API HERE =====================

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getBookingDetails(@PathVariable Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));

        Map<String, Object> response = new HashMap<>();
        response.put("guestName", booking.getGuestName());
        response.put("hotelName", booking.getProperty().getName());
        response.put("city", booking.getProperty().getCity().getName());
        response.put("country", booking.getProperty().getCountry().getName());
        response.put("checkIn", booking.getCheckInDate());
        response.put("checkOut", booking.getCheckOutDate());
        response.put("roomType", booking.getTypeOfRoom());
        response.put("email", booking.getEmail());
        response.put("mobile", booking.getMobile());
        response.put("totalPrice", booking.getTotal_price());

        return ResponseEntity.ok(response);
    }

    // Booking for the logged in user which user can see any time - myBookings
    @GetMapping("/myBookings")
    public ResponseEntity<List<MyBookingDto>> getMyBookings(@AuthenticationPrincipal AppUser user) {
        List<Booking> bookings = bookingRepository.findByAppUser(user);

        List<MyBookingDto> dtos = bookings.stream().map(booking -> {
            MyBookingDto dto = new MyBookingDto();
            dto.setBookingId(booking.getId());
            dto.setGuestName(booking.getGuestName());
            dto.setMobile(booking.getMobile());
            dto.setEmail(booking.getEmail());
            dto.setTotalPrice(booking.getTotal_price());
            dto.setNoOfGuests(booking.getNoOfGuests());
            dto.setTotalNights(booking.getTotalNights());
            dto.setCheckInDate(booking.getCheckInDate());
            dto.setCheckOutDate(booking.getCheckOutDate());
            dto.setRoomType(booking.getTypeOfRoom());
            dto.setRoomType(booking.getTypeOfRoom());

            //  Add status mapping
            dto.setStatus(booking.getStatus());


            // 🔹 fetch details from Property entity
            if (booking.getProperty() != null) {
                dto.setHotelName(booking.getProperty().getName()); //  hotel name
                if (booking.getProperty().getCity() != null) {
                    dto.setCity(booking.getProperty().getCity().getName()); //  city name
                }
                if (booking.getProperty().getCountry() != null) {
                    dto.setCountry(booking.getProperty().getCountry().getName()); //  country name
                }
            }

            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }

       //  update status - booking is ACTIVE or COMPLETED  or CANCELLED
       @PutMapping("/{id}/cancel")
       public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
           bookingService.cancelBooking(id);
           return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully"));
       }


}

// we can use this for exception handling:

// Property property = propertyRepository.findById(propertyId)
//        .orElseThrow(() -> new IllegalArgumentException("Property not found"));



