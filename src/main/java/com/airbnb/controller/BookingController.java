package com.airbnb.controller;

import com.airbnb.entity.AppUser;
import com.airbnb.entity.Booking;
import com.airbnb.entity.Property;
import com.airbnb.entity.Room;
import com.airbnb.payload.BookingResponseDTO;
import com.airbnb.repository.BookingRepository;
import com.airbnb.repository.PropertyRepository;
import com.airbnb.repository.RoomRepository;
import com.airbnb.service.PDFService;
import com.airbnb.service.SmsService;
import com.airbnb.service.ValidateBookingDatesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public BookingController(RoomRepository roomRepository, PropertyRepository propertyRepository, BookingRepository bookingRepository, PDFService pdfService, SmsService smsService, ValidateBookingDatesService validateBookingDatesService) {
        this.roomRepository = roomRepository;
        this.propertyRepository = propertyRepository;
        this.bookingRepository = bookingRepository;
        this.pdfService = pdfService;
        this.smsService = smsService;
        this.validateBookingDatesService = validateBookingDatesService;
    }
    // before i do the booking --> search whether the room is available or not upon supplying -->
    // propertyId and roomType


    // URL --> http://localhost:8080/api/v1/booking/createBooking?propertyId=1&roomType=executive
    // URL --> http://localhost:8080/api/v1/booking/createBooking?propertyId=1&roomType=delux
    @PostMapping("/createBooking")
    public ResponseEntity<?> createBooking(
            @RequestParam long propertyId,
            @RequestParam String roomType,
            @RequestBody @Valid Booking booking,
            @AuthenticationPrincipal AppUser user
            ){

        // @Valid --> it will perform validation on the Booking object in the @RequestBody
        // before the controller method executes --> see details at last
        // When @Valid is used on a parameter like @RequestBody Booking booking, the validation
        // framework inspects the Booking object to ensure all the constraints specified in its class are satisfied.

        if (booking.getCheckOutDate().isBefore(booking.getCheckInDate())) {
            return new ResponseEntity<>("Check-out date cannot be before check-in date.", HttpStatus.BAD_REQUEST);
        }

        // What if both dates are the same? check for that.
        if (!booking.getCheckOutDate().isAfter(booking.getCheckInDate())) {
            return new ResponseEntity<>("Check-out date must be after check-in date.", HttpStatus.BAD_REQUEST);
        }

        // below Check Future or Past Date --> using Custom Validation logic without annotation

//        validateBookingDatesService.validateBookingDates(booking.getCheckInDate(), booking.getCheckOutDate());

// Save the booking to the database


        // checkin and checkout date comes from --> booking
        // below code will give me property details according to Propert Id
        // exception handling for property not found --> see at last
        Property property = propertyRepository.findById(propertyId).get();

//        Property property = propertyRepository.findById(propertyId)
//                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        // below it returns date as a List --> below it is getting all the dates and every date --> i need
        // to check the --> room availability
        // if we give "checkInDate": "2024-11-20"  AND "checkOutDate": "2024-11-23"   THEN
        //  Below object --> datesBetween --> will have --> 4 dates --> 2024-11-20, 2024-11-21
        // 2024-11-22, 2024-11-23   AND one by one these daates will go into --> For loop



        List<LocalDate> datesBetween = getDatesBetween(booking.getCheckInDate(), booking.getCheckOutDate());

        // below whenever we get the room --> I would like to add that to a List --> because
        // if the rooms are available --> all the 4 rooms data (object) --> should be stored
        // in a List --> and then from that object one by one --> i have to remove the count
        // column wise from there --> so in this case i will build another List:
        // then take that --> rooms object after the if condition
        List<Room> rooms = new ArrayList<>();

        for (LocalDate date: datesBetween) {

            // below there will not be a List of rooms --> here combination of propertyId and
            // type of room and date will give me only one record --> so it will return only one room object

            // step: 1 --> check the room
            Room room = roomRepository.findByPropertyIdAndTypeAndDate(propertyId, roomType, date);
            // Step: 2 --> if rooms are available
            // if condition is checking room availability

            // Below if we give the date which is not available in database for property booking:

            if (room == null) {
                return new ResponseEntity<>("Room not found for the given property and room type on date: " + date, HttpStatus.BAD_REQUEST);
            }

            // below if rooms are not available or room count should not be negetive

            if (room.getCount() <= 0) {
                // Below if count is zero --> it will remove the List data and let us not keep the list data
                // populated --> so by doing this it will remove all the rooms object --> means
                // in the rooms (rooms.add(room)) object --> remove all data --> so that list is not
                // populated with the data
//                rooms.removeAll(rooms);
                return new ResponseEntity<>("No rooms available", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // when if condition is false --> I would like to add here room object in the List
            // so I am creating a List --> which will have rooms availability details in it
            // if there are 4 rooms available --> those 4 objects of the room will be present in it
            // this will have all rooms details
            rooms.add(room);

        }
        // Booking:
        // we have different price in different date in room table
        // keep total=0 --> outside for loop --> otherwise total will become zero evreytime
        float total=0;
        for (Room room: rooms){
            total = total + room.getPrice();
            // total --> this will take 1st day room price then 2nd day room price and then
            // 3rd day room price --> so total price from checkin to checkout day
            // logic will varry based on implementation(business logic) --> here I am assuming
            // everyday the price is different --> But sometimes they keep the same price -->But
            // sometimes price may varry depending on the season
            // if prices are same --> then better you create another table --> and do the
            // mapping between them
        }
        // it will print total price of all three days
//        System.out.println(total);
        booking.setTotal_price(total);
        // set the property id in booking table:
        booking.setProperty(property);
        // Appuser has to be a JWT Token --> which will populate the Appuser:
        booking.setAppUser(user);
        booking.setTypeOfRoom(roomType);
        Booking savedBooking = bookingRepository.save(booking);


        // Step: 3 --> update the room
        // if rooms are available --> then do the booking --> but everytime you do the booking
        // update the room --> so first update:
        // below for loop --> will decrease the room count on every date --> means from Checkin
        // date to Checkout date, if chekin --> 23  AND checkou --> 25  THEN it will decrease
        // the room count of --> 23, 24  AND 25 all the dates
        // so below we have updated the room for List of dates
        if (savedBooking!=null) {
            for (Room room : rooms) {
                int availableRooms = room.getCount();
                room.setCount(availableRooms - 1);
                roomRepository.save(room);
            }
        }
        // Generate PDF Document --> trigger PDF Service creation from here
        // and then send the attachment to email --> pankajjasola16@gmail.com --> in
        pdfService.generatePdf(savedBooking);

        // Send SMS Confirmation

        // for below use this --> "mobile": "+918850372019"
//        smsService.sendSms(booking.getMobile(),"Your booking is confirmed, your booking id is: "+booking.getId());
        smsService.sendSms("+91"+booking.getMobile(),"Your booking is confirmed, your booking id is: "+booking.getId());
        // above i am using --> +91 --> so need to write +91 in JSON --> But this is not the righr way
        // "mobile": "8850372019"

        // Send Whatsap Confirmation

        smsService.sendWhatsApp(booking.getMobile(), "Your booking is confirmed, your booking id is: "+booking.getId());


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
    }

    //
    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;

        // currentDate.isAfter(endDate) --> means current date is  20 and end date is 23
        // so here it is asking --> is 20 after 23 --> no so condition is false but if we write:
        // !currentDate.isAfter(endDate --> true --> because we are using --> ! --> not
        while (!currentDate.isAfter(endDate)) {

            dates.add(currentDate);

            currentDate = currentDate.plusDays(1);
        }

        return dates;
    }

}

// we can use this for exception handling:

// Property property = propertyRepository.findById(propertyId)
//        .orElseThrow(() -> new IllegalArgumentException("Property not found"));
