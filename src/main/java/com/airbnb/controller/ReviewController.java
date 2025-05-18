package com.airbnb.controller;

import com.airbnb.entity.AppUser;
import com.airbnb.entity.Property;
import com.airbnb.entity.Review;
import com.airbnb.exception.ResourceNotFoundException;
import com.airbnb.payload.ReviewDto;
import com.airbnb.repository.PropertyRepository;
import com.airbnb.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    // URL will be for below --> http://localhost:8080/api/v1/reviews/createReview
    // But when you call this method --> without the user logged in --> this cannot
    // move --> so we know how to do this in Spring Security -->
    // so for that add one --> RequestMatcher in --> SecurityConfig.java --> give URL -->
    // hasRole and give role as --> user --> so user can give the review

    // Why we are using PropertyRepository --> because in this review object --> review is there
    // But when you save the review --> it is not just the review you will save --> you will save
    // foreign keys of --> user and the property foreign key

    private ReviewRepository reviewRepository;
    private PropertyRepository propertyRepository;

    public ReviewController(ReviewRepository reviewRepository, PropertyRepository propertyRepository) {
        this.reviewRepository = reviewRepository;
        this.propertyRepository = propertyRepository;
    }

    // URL --> http://localhost:8080/api/v1/reviews/createReview?propertyId=1
    @RequestMapping("/createReview")
    // public ResponseEntity<Review> createReview( --> It returns String and Review object so ?
    public ResponseEntity<?> createReview(
            @RequestBody Review review,
            @AuthenticationPrincipal AppUser user,
            @RequestParam long propertyId
    ){

        // Above we logged in as mike --> then from JSON content give --> review (rating and
        //  description) --> supply propetyId in URL --> and click on send by supplying --> JWT
        // Token --> And automatically this (AppUser user) will have --> mike details -->
        // because of --> @AuthenticationPrincipal Annotation

        // from POSTMAN --> I did not supply --> user details --> user deatils automatically
        // is coming to the backend, so what @AuthenticationPrincipal does --> Moment you
        // supply JWT Token to the URL --> this Annotation gets the --> user deatils and telling
        // this is the current user logged in --> this is how we track --> which user is logged in
        // if we will not track --> which user is logged in --> we will not be able to give the
        // relevant user data --> which means --> when i login you should give me only my review Details
        // that is possible only --> when i am able to track the current user logged in data

//        System.out.println(user.getName());  // Output --> mike
//        System.out.println(user.getEmail()); // Output --> mike@gmail.com

        // below also create exception --> resource not found by yourself --> orElseThrow
        // Create exception class and ControllerAdvice class
//        Property property = propertyRepository.findById(propertyId).get();
        // instead of using above code use below code for handling Exception
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                ()-> new ResourceNotFoundException("property with this id :"+propertyId+" does not exist")
        );
        // below we are supplying directly object not userId and propertyId
        Review reviewDetails = reviewRepository.findByUserAndProperty(user, property);
        if (reviewDetails!=null){
            return new ResponseEntity<>("Review Exists", HttpStatus.CREATED);
        }
        // below we will set the user id and proprty id in review table which is foriegn key
        review.setAppUser(user);
        // and user details(user) --> automatically comes here in --> @AuthenticationPrincipal AppUser user
        // whoever logs in --> (this is like a session management happening)
        review.setProperty(property);
        Review savedreview = reviewRepository.save(review);

        ReviewDto dto = new ReviewDto();
        dto.setId(savedreview.getId());
        dto.setRating(savedreview.getRating());
        dto.setDescription(savedreview.getDescription());
        // no need to send user id and property id:
//        dto.setUserId(user.getId());
//        dto.setPropertyId(property.getId());

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // Now what will happen --> this particular object --> (appUser user) --> data
    // Automatically get populated based on the JWT Token that I will supply

    // @AuthenticationPrincipal --> The moment you Login --> so after login you will
    // call this review link --> with the JWT Token --> when you call this method with
    // JWT Token --> user details will automatically go to this object --> there is
    // nothing you need to do this
    // so this will track --> the current user was logged in --> you need not write any code

    // Below ADMIN can see the review of everyone but user can see only his own review
//    Now when user logged in only he can see his review  build that
//    Means  you login in your bank account  you are able to see your bank balance


    // To get all the reviews of user who have logged in --> here mike is logged in
    // URL --> http://localhost:8080/api/v1/reviews/userReviews
    // send URL with JWT Token
    @GetMapping("/userReviews")
    public List<Review> listReviewsOfUser(
            // below is to check which user has logged in --> and this user id give in --> @GetMapping
            @AuthenticationPrincipal AppUser user
    ){
        // here simply give the user object
        List<Review> reviews = reviewRepository.findByReviewsByUser(user);


        return reviews;
        // make return as response entity
    }
}
