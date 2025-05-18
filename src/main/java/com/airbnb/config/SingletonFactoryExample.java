package com.airbnb.config;

public class SingletonFactoryExample {

//    Tell me where I have used singleton , factory and observer design patten in the Booking
//    Controller class

//    Let's analyze where the Singleton, Factory, and Observer design patterns might have been
//    used in the provided code. While these patterns may not be explicitly defined, their
//    principles might be applied indirectly in the Spring framework components.

                            //    1. Singleton Pattern

//    The Singleton pattern ensures a class has only one instance and provides a global point of access to it.

//    Where Singleton is Used:
//                      1 -->   Spring-Managed Beans:

//    The following services and repositories are managed as Spring beans:
//    RoomRepository, PropertyRepository, BookingRepository
//            PDFService, SmsService, ValidateBookingDatesService
//    Spring beans are by default Singletons. Spring ensures that only one instance of each bean exists in the application context.

//                                   Example:

    // private RoomRepository roomRepository;
    // private PropertyRepository propertyRepository;
    // private BookingRepository bookingRepository;
    // private PDFService pdfService;
    // private SmsService smsService;
    // private ValidateBookingDatesService validateBookingDatesService;

// Above These are injected into the controller by Spring and reused across the application.

//                     2 --> Utility Methods:

//    The static method getDatesBetween in your controller is effectively a singleton-like
//    utility method since it does not require instantiation of the class and is globally
//    accessible.

                             //    2. Factory Pattern

//    The Factory pattern provides an interface for creating objects but allows subclasses to
//    alter the type of objects that will be created.

//    Where Factory is Used:
//                   1 --> Repository Methods:

//    The repository methods, such as findById and findByPropertyIdAndTypeAndDate, act as factories
//    for fetching specific instances of Property or Room from the database.
//    Example:

    //  Property property = propertyRepository.findById(propertyId).get();
    //  Room room = roomRepository.findByPropertyIdAndTypeAndDate(propertyId, roomType, date);

// Above These methods abstract the creation (or retrieval) logic of objects, aligning with the Factory pattern.

//                   2 --> DTO Creation:

//    The BookingResponseDTO is created as part of the response, which can be seen as a
//    factory-like approach:

    // BookingResponseDTO responseDTO = new BookingResponseDTO(
            // savedBooking.getId(),
            // booking.getGuestName(),
            // booking.getMobile(),
            // booking.getEmail(),
            // booking.getTotal_price(),
            // booking.getNoOfGuests(),
            // booking.getTotalNights(),
            // booking.getCheckInDate(),
            // booking.getCheckOutDate(),
            // "Your booking is successfully created."
    // );

// Above While not a true Factory pattern, it encapsulates the construction of a complex object.

                              //    3. Observer Pattern

//    The Observer pattern defines a one-to-many dependency between objects so that when one
//    object changes state, all its dependents are notified and updated automatically.

//    Where Observer is Used:
//                     1 --> Spring Events (Indirectly):

//    Spring Security, SMS service, and PDF generation in your code follow an event-driven approach:
//    PDFService and SmsService observe the completion of the booking and react accordingly
//    by generating a PDF and sending SMS/WhatsApp notifications.
//    Example:

        // pdfService.generatePdf(savedBooking);
        // smsService.sendSms("+91" + booking.getMobile(), "Your booking is confirmed, your booking id is: " + booking.getId());
        // smsService.sendWhatsApp(booking.getMobile(), "Your booking is confirmed, your booking id is: " + booking.getId());

//    These methods act as observers that are triggered by the state change (i.e., a booking is created).
//                      2 --> Room Availability Check:

//    The Room objects are evaluated for availability in a loop, and their count is updated
//    based on the booking. While not a pure Observer pattern, the state change of Room
//    influences subsequent operations:

    // room.setCount(availableRooms - 1);
    // roomRepository.save(room);



}
