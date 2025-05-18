package com.airbnb.controller;

import com.airbnb.entity.Country;
import com.airbnb.repository.CountryRepository;
import com.airbnb.service.CountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/country")
public class CountryController {

    private CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    // url: http://localhost:8080/api/v1/country/addCountry
    @RequestMapping("/addCountry")
    public ResponseEntity<?> AddCountry(@RequestBody Country country){

        return countryService.addCountry(country);
    }
}
