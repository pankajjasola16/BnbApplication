package com.airbnb.service;

import com.airbnb.entity.City;
import com.airbnb.entity.Country;
import com.airbnb.exception.ResourceNotFoundException;
import com.airbnb.repository.CityRepository;
import com.airbnb.repository.CountryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CityService {
    private CityRepository cityRepository;
    private CountryRepository countryRepository;

    public CityService(CityRepository cityRepository, CountryRepository countryRepository) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    // http://localhost:8080/api/v1/city/addCity?countryName=India
    public ResponseEntity<?> addCity(String countryName, City city) {

        Country country = countryRepository.findByCountryName(countryName).orElseThrow(
                () -> new ResourceNotFoundException("This Country does not exist")
        );

        Optional<City> opCity = cityRepository.findByCityName(city.getName());

        if (opCity.isPresent()){
            return new ResponseEntity<>("this City name: "+city.getName()+ " is already exist", HttpStatus.FOUND);
        }
        // set foreign key
        city.setCountry(country);
        City savedCity = cityRepository.save(city);
        return new ResponseEntity<>(savedCity, HttpStatus.CREATED);
    }
}
