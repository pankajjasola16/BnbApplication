package com.airbnb.controller;

import com.airbnb.entity.*;
import com.airbnb.payload.PropertyDTO;
import com.airbnb.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import com.airbnb.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;



// here we are doing shortcut --> but create --> service layer dto and return the data back
// Now we have search feature where w/api/v1/reviews/createReview?propertyId=2hatever city name i enter All the properties
// related to that city i can get --> search feature i am able to built because --> we are
// strictly following ER Diagram --> without ER Digram building a search feature is difficult
@RestController
@RequestMapping("/api/v1/property")
public class PropertyController {

    private PropertyRepository propertyRepository;
    private CityRepository cityRepository;
    private CountryRepository countryRepository;
    private RoomRepository roomRepository;
    private ReviewRepository reviewRepository;

    public PropertyController(PropertyRepository propertyRepository, CityRepository cityRepository, CountryRepository countryRepository, RoomRepository roomRepository, ReviewRepository reviewRepository) {
        this.propertyRepository = propertyRepository;
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.roomRepository = roomRepository;
        this.reviewRepository = reviewRepository;
    }

    // URL --> http://localhost:8080/api/v1/property/propertyresult?city=Ooty
    // new URL --> http://localhost:8080/api/v1/property/propertyresult?name=India
    // new URL --> http://localhost:8080/api/v1/property/propertyresult?name=Ooty
//    @GetMapping("/propertyresult")
//    public List<Property> searchProperty(
////            @RequestParam("city") String cityName
//            // use below code for based on city or country and above code only for city
//            @RequestParam("name") String cityName
//    )
//    {
//        return propertyRepository.searchProperty(cityName);
//
//    }

      // Above is without Pagination and below is with Paginatio

                            //  Pagination and price based search

// http://localhost:8080/api/v1/propertyresult?name=ooty&page=0&size=3&minPrice=1200&maxPrice=2000
@GetMapping("/propertyresult")
//public Page<Property> searchProperty(
public Page<PropertyDTO> searchProperty(
        @RequestParam("name") String cityOrCountry,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size,
        @AuthenticationPrincipal AppUser user   // <-- logged-in user
) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Property> properties;

    if (minPrice != null && maxPrice != null) {
        Double actualMin = propertyRepository.findMinRoomPrice(cityOrCountry);
        Double actualMax = propertyRepository.findMaxRoomPrice(cityOrCountry);
        if (actualMin == null || actualMax == null) {
            return Page.empty(pageable);
        }

//        if (maxPrice < actualMin) {
//            // Only fetch rooms at the single cheapest price
//            return propertyRepository.searchPropertyWithPrice(cityOrCountry, actualMin, actualMin, pageable);
//        } else if (minPrice > actualMax) {
//            // Only fetch rooms at the single most expensive price
//            return propertyRepository.searchPropertyWithPrice(cityOrCountry, actualMax, actualMax, pageable);
//        } else {
//            // Regular clamped range
//            double effectiveMin = Math.max(minPrice, actualMin);
//            double effectiveMax = Math.min(maxPrice, actualMax);
//            return propertyRepository.searchPropertyWithPrice(cityOrCountry, effectiveMin, effectiveMax, pageable);
//        }
//    }
//
//    return propertyRepository.searchProperty(cityOrCountry, pageable);

        if (maxPrice < actualMin) {
            properties = propertyRepository.searchPropertyWithPrice(cityOrCountry, actualMin, actualMin, pageable);
        } else if (minPrice > actualMax) {
            properties = propertyRepository.searchPropertyWithPrice(cityOrCountry, actualMax, actualMax, pageable);
        } else {
            double effectiveMin = Math.max(minPrice, actualMin);
            double effectiveMax = Math.min(maxPrice, actualMax);
            properties = propertyRepository.searchPropertyWithPrice(cityOrCountry, effectiveMin, effectiveMax, pageable);
        }
    } else {
        properties = propertyRepository.searchProperty(cityOrCountry, pageable);
    }

                  //   option 1 - without dto

    //  Set minRoomPrice for each property
//    for (Property property : properties.getContent()) {
//        Double minPriceForProperty = roomRepository.findMinPriceByPropertyId(property.getId());
//        property.setMinRoomPrice(minPriceForProperty);
//
//        // Set average rating
//        Double avgRating = reviewRepository.calculateAverageRating(property.getId());
//        property.setRating(avgRating != null ? avgRating : 0.0);
//
//        // Optionally, if user is logged in, fetch user's rating
//        // AppUser currentUser = getLoggedInUserSomehow();
//        // Review userReview = reviewRepository.findByUserAndProperty(currentUser, property);
//        // property.setUserRating(userReview != null ? userReview.getRating() : 0);
//    }
//
//    return properties;

                       //  option 2  with dto

    // Get current logged-in user (implement this as per your security)
    // Map Property -> PropertyDTO
    Page<PropertyDTO> dtoPage = properties.map(property -> {
        Double minPriceForProperty = roomRepository.findMinPriceByPropertyId(property.getId());
        property.setMinRoomPrice(minPriceForProperty);

        Double avgRating = reviewRepository.calculateAverageRating(property.getId());
        Integer userRating = null;
        if (user != null) {
            Review userReview = reviewRepository.findByUserAndProperty(user, property);
            if (userReview != null) userRating = userReview.getRating();
        }

        return new PropertyDTO(property, avgRating, userRating);
    });

    return dtoPage;

}



    // page is 0-indexed: page 0 is the first page, page 1 is the second, etc.
    // You can change size default value to 3 to always return 3 results per page unless overridden.

    // URL --> http://localhost:8080/api/v1/property/addProperty?cityId=1&countryId=1
    // URL --> http://localhost:8080/api/v1/property/addProperty?cityName=Ooty&countryName=India
    @PostMapping("/addProperty")
    public ResponseEntity<?> addProperty(
//    public String addProperty(
//            @RequestParam long cityId,
//            @RequestParam long countryId,
            @RequestParam String cityName,
            @RequestParam String countryName,
             @RequestBody Property property)  // use this when you are mot using html page
            // below code --> when I add the property then I want separate page for showing what property I have added
//            @ModelAttribute Property property, Model model)  // Use @ModelAttribute for form binding
    {

        City city = cityRepository.findByCityName(cityName).orElseThrow(
                () -> new ResourceNotFoundException("City is not available please add the city"));

        Country country = countryRepository.findByCountryName(countryName).orElseThrow(
                () -> new ResourceNotFoundException("Country is not available please add Country")
        );

        // here also check according to the --> city name because it may happen that city
        // is present but we are finding according to city id and city id is not present
        // so also check according to City name and Country name

//        City city = cityRepository.findById(cityId).orElseThrow(
//                () -> new ResourceNotFoundException("City is not available please add the city"));
//
//        Country country = countryRepository.findById(countryId).orElseThrow(
//                () -> new ResourceNotFoundException("Country is not available please add Country"));


//        Optional<Property> opDetails = propertyRepository.findByPropertyNameAndCityIdAndCountryId(
//                property.getName(), cityId, countryId);

        Optional<Property> opDetails = propertyRepository.findByPropertyNameAndCityIdAndCountryId(
                property.getName(), city.getId(), country.getId());

        if (opDetails.isPresent()){
         //       HttpStatus.FOUND is 302 redirect status — not the best for errors.
          //       Use 409 Conflict instead for duplicates.
//            return new ResponseEntity<>("this property name: "+property.getName()+ " is already exist", HttpStatus.FOUND);
            return new ResponseEntity<>("this property name: "+property.getName()+ " already exists", HttpStatus.CONFLICT);

//
        }




        property.setCity(city);
        property.setCountry(country);
        Property savedProperty = propertyRepository.save(property);
        return new ResponseEntity<>(savedProperty, HttpStatus.CREATED);

//        model.addAttribute("savedProperty", savedProperty);
//        return "your-added-property"; // open result page
    }

    // url: http://localhost:8080/api/v1/property/deleteProperty?propertyId=1

    @DeleteMapping("/deleteProperty")
    public ResponseEntity<?> deleteProperty(
            @RequestParam Long propertyId) {

           // old code
//        if (!propertyRepository.existsById(propertyId)) {
//            return ResponseEntity.notFound().build();
        Optional<Property> optionalProperty = propertyRepository.findById(propertyId);
        if (optionalProperty.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

//        }

//        // Delete associated reviews manually
//        List<Review> reviews = reviewRepository.findByPropertyId(propertyId);
//        reviewRepository.deleteAll(reviews);

        // for above code --> also create --> findByPropertyId --> in reviewRepository

        propertyRepository.deleteById(propertyId); // Cascade will handle deleting associated Rooms
        return ResponseEntity.ok("Property and associated Rooms deleted successfully.");
    }

    @GetMapping("/properties-with-ratings")
    public ResponseEntity<?> getPropertiesWithRatings(@AuthenticationPrincipal AppUser user) {
        List<Property> properties = propertyRepository.findAll();

        List<Map<String, Object>> result = properties.stream().map(p -> {
            Double avgRating = reviewRepository.calculateAverageRating(p.getId());
            if (avgRating == null) avgRating = 0.0;

            Review userReview = reviewRepository.findByUserAndProperty(user, p);

            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("country", p.getCountry().getName());
            map.put("city", p.getCity().getName());
            map.put("avgRating", avgRating);
            map.put("userRating", (userReview != null ? userReview.getRating() : 0));
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

}


//@RequestMapping("/api/v1/dummy")

//public class dummyController {


    // @GetMapping("/getMessage")
//     public String getMessage()
//    {
//        return "Hello World";
//    }


// this we are doing to check Spring Security and for that we have added one dependency:
// <artifactId>spring-boot-starter-security</artifactId>
// this controller for running the output in the browser
// Give URL in Browser --> http://localhost:8080/api/v1/dummy
//username --> user    and password will come in console
// logout --> http://localhost:8080/logout

