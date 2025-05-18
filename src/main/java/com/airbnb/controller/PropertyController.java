package com.airbnb.controller;

import com.airbnb.entity.City;
import com.airbnb.entity.Country;
import com.airbnb.entity.Property;
import com.airbnb.exception.ResourceNotFoundException;
import com.airbnb.repository.CityRepository;
import com.airbnb.repository.CountryRepository;
import com.airbnb.repository.PropertyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    public PropertyController(PropertyRepository propertyRepository, CityRepository cityRepository, CountryRepository countryRepository) {
        this.propertyRepository = propertyRepository;
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    // URL --> http://localhost:8080/api/v1/property/propertyresult?city=Ooty
    // new URL --> http://localhost:8080/api/v1/property/propertyresult?name=India
    // new URL --> http://localhost:8080/api/v1/property/propertyresult?name=Ooty
    @GetMapping("/propertyresult")
    public List<Property> searchProperty(
//            @RequestParam("city") String cityName
            // use below code for based on city or country and above code only for city
            @RequestParam("name") String cityName
    )
    {
        return propertyRepository.searchProperty(cityName);

    }

    // URL --> http://localhost:8080/api/v1/property/addProperty?cityId=1&countryId=1
    // URL --> http://localhost:8080/api/v1/property/addProperty?cityName=Ooty&countryName=India
    @PostMapping("/addProperty")
    public ResponseEntity<?> addProperty(
//            @RequestParam long cityId,
//            @RequestParam long countryId,
            @RequestParam String cityName,
            @RequestParam String countryName,
            @RequestBody Property property
    ){

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
//            return new ResponseEntity<>("this property name: "+property.getName()+ " is already exist", HttpStatus.FOUND);
            return new ResponseEntity<>("this property name: "+property.getName()+ " is already exist", HttpStatus.FOUND);
        }




        property.setCity(city);
        property.setCountry(country);
        Property savedProperty = propertyRepository.save(property);
        return new ResponseEntity<>(savedProperty, HttpStatus.CREATED);
    }

    // url: http://localhost:8080/api/v1/property/deleteProperty?propertyId=1

    @DeleteMapping("/deleteProperty")
    public ResponseEntity<?> deleteProperty(
            @RequestParam Long propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            return ResponseEntity.notFound().build();
        }

//        // Delete associated reviews manually
//        List<Review> reviews = reviewRepository.findByPropertyId(propertyId);
//        reviewRepository.deleteAll(reviews);

        // for above code --> also create --> findByPropertyId --> in reviewRepository

        propertyRepository.deleteById(propertyId); // Cascade will handle deleting associated Rooms
        return ResponseEntity.ok("Property and associated Rooms deleted successfully.");
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
