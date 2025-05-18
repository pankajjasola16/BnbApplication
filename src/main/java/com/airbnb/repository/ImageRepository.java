package com.airbnb.repository;

import com.airbnb.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

    // here we will develop finder method --> to get the images for the particular id -->
    // findByPropertyId --> and it will return back the list of --> images URL
}