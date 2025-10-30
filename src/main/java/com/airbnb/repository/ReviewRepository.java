package com.airbnb.repository;

import com.airbnb.entity.AppUser;
import com.airbnb.entity.Property;
import com.airbnb.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // here we will write code --> so that user can give review only once for a particular property

   @Query("Select r from Review r where r.property=:property and r.appUser=:user")
    Review findByUserAndProperty(
        // directly supply the user and property object from ReviewController
           @Param("user") AppUser user,
            @Param("property") Property property
    );

   // when we will call the above method --> we will directly supply the objects into it (user & property)
    // I will not supply property id and user id this is also a way to search in a table
    // So I am directly giving the object addresses and performing a search

    // r.appUser=:user  --> here we are directly comparing the reference variable
    @Query("Select r from Review r where r.appUser=:user")
    List<Review> findByReviewsByUser(
            // I can also supply the id here But we will supply object
            @Param("user") AppUser user
    );

    // for calculating average rating by different users
 @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property.id = :propertyId")
 Double calculateAverageRating(@Param("propertyId") long propertyId);

}
