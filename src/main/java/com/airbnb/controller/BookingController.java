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


    // URL --> http://localhost:8080/api/v1/booking/createBooking?propertyId=1&roomType=executive
    // URL --> http://localhost:8080/api/v1/booking/createBooking?propertyId=1&roomType=delux
    @PostMapping("/createBooking")
    public ResponseEntity<?> createBooking(
            @RequestParam long propertyId,
            @RequestParam String roomType,
            @RequestBody @Valid Booking booking,
            @AuthenticationPrincipal AppUser user
            ){

        

        if (booking.getCheckOutDate().isBefore(booking.getCheckInDate())) {
            return new ResponseEntity<>("Check-out date cannot be before check-in date.", HttpStatus.BAD_REQUEST);
        }

        // What if both dates are the same? check for that.
        if (!booking.getCheckOutDate().isAfter(booking.getCheckInDate())) {
            return new ResponseEntity<>("Check-out date must be after check-in date.", HttpStatus.BAD_REQUEST);
        }

        
        Property property = propertyRepository.findById(propertyId).get();

//        Property property = propertyRepository.findById(propertyId)
//                .orElseThrow(() -> new IllegalArgumentException("Property not found"));


        List<LocalDate> datesBetween = getDatesBetween(booking.getCheckInDate(), booking.getCheckOutDate());

        List<Room> rooms = new ArrayList<>();

        for (LocalDate date: datesBetween) {

            
            Room room = roomRepository.findByPropertyIdAndTypeAndDate(propertyId, roomType, date);
            

            if (room == null) {
                return new ResponseEntity<>("Room not found for the given property and room type on date: " + date, HttpStatus.BAD_REQUEST);
            }

            // below if rooms are not available or room count should not be negetive

            if (room.getCount() <= 0) {
            
//                rooms.removeAll(rooms);
                return new ResponseEntity<>("No rooms available", HttpStatus.INTERNAL_SERVER_ERROR);
            }
           
            rooms.add(room);

        }
        
        float total=0;
        for (Room room: rooms){
            total = total + room.getPrice();
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
       
        if (savedBooking!=null) {
            for (Room room : rooms) {
                int availableRooms = room.getCount();
                room.setCount(availableRooms - 1);
                roomRepository.save(room);
            }
        }
        
        pdfService.generatePdf(savedBooking);

        // Send SMS Confirmation
  
        smsService.sendSms("+91"+booking.getMobile(),"Your booking is confirmed, your booking id is: "+booking.getId());      

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

        while (!currentDate.isAfter(endDate)) {

            dates.add(currentDate);

            currentDate = currentDate.plusDays(1);
        }

        return dates;
    }

}


