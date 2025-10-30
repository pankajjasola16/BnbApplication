package com.airbnb.repository;

import com.airbnb.entity.AppUser;
import com.airbnb.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByAppUser(AppUser user);
}
