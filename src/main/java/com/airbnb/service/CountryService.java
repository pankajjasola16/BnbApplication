package com.airbnb.service;

import com.airbnb.entity.Country;
import com.airbnb.repository.CountryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CountryService {

    private CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public ResponseEntity<?> addCountry(Country country) {

        Optional<Country> opCountry = countryRepository.findByCountryName(country.getName());

        if (opCountry.isPresent()){

            return new ResponseEntity<>("Country already exist", HttpStatus.FOUND);
        }

        Country savedCountry = countryRepository.save(country);

        return new ResponseEntity<>(savedCountry, HttpStatus.CREATED);
    }
}
