package com.airbnb.controller;

import com.airbnb.entity.City;
import com.airbnb.entity.Country;
import com.airbnb.repository.CityRepository;
import com.airbnb.service.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/city")
public class CityController {

    private CityService cityService;


    public CityController(CityService cityService) {
        this.cityService = cityService;

    }

    // http://localhost:8080/api/v1/city/addCity?countryName=India
    // http://localhost:8080/api/v1/city/addCity?countryId=1
    @PostMapping("/addCity")
    public ResponseEntity<?> AddCity(
            @RequestParam String countryName,
            @RequestBody City city
    ){
        return cityService.addCity(countryName, city);

        //              OR

//        ResponseEntity<?> responseEntity = cityService.addCity(countryName, city);
//
//        return responseEntity;
    }


    // http://localhost:8080/api/v1/city/addCity?countryId=1
    // above we can also search the country by using country id
}
