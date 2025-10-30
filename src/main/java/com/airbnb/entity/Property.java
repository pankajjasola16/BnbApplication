package com.airbnb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "property")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "number_of_guests", nullable = false)
    private Integer numberOfGuests;

    @Column(name = "number_of_beds", nullable = false)
    private Integer numberOfBeds;

    @Column(name = "number_of_bathrooms", nullable = false)
    private Integer numberOfBathrooms;

    @Column(name = "number_of_bedrooms", nullable = false)
    private Integer numberOfBedrooms;



    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    //  below codes we are using --> if we want to delete property --> then also delete
    // below records belong to that property

    // Add a bidirectional relationship with Room
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Room> rooms = new ArrayList<>();

    // Add helper methods for managing the relationship
    public void addRoom(Room room) {
        rooms.add(room);
        room.setProperty(this);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
        room.setProperty(null);
    }

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    public void addReview(Review review) {
        reviews.add(review);
        review.setProperty(this); // Maintain bidirectional relationship
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setProperty(null); // Break the bidirectional relationship
    }

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Image> images = new ArrayList<>();

    // Add helper methods to maintain consistency in bidirectional relationships
    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setProperty(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setProperty(null);
    }

    public void addImage(Image image) {
        images.add(image);
        image.setProperty(this);
    }

    public void removeImage(Image image) {
        images.remove(image);
        image.setProperty(null);
    }

    // This field is not saved to the database.  Below
    //It will be used just to send the cheapest room price with each property.

    @Transient
    private Double minRoomPrice;

    public Double getMinRoomPrice() {
        return minRoomPrice;
    }

    public void setMinRoomPrice(Double minRoomPrice) {
        this.minRoomPrice = minRoomPrice;
    }

    @Transient
    private Double rating; // average rating

    @Transient
    private Double userRating; // current logged-in user's rating (optional)

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Double getUserRating() { return userRating; }
    public void setUserRating(Double userRating) { this.userRating = userRating; }

//    @Transient → not persisted in DB, just sent to frontend.
//
//    You can calculate and set rating in PropertyController when you fetch the properties.

}

// Parent table  It does not have the foreign key
// Child table  It has the foreign key
// Parent  country    AND    Child    property
// so property is a child for Country

// property can be Many so here foreign keys will be there --> and it becomes child



//Here's why helper methods are useful:
//
//Maintain Bidirectional Consistency: When you add or remove a Review from a Property, helper methods ensure that both sides of the relationship (Property and Review) are updated properly.
//Reduce Boilerplate: They encapsulate repetitive code and make your entities easier to use.
//Prevent Errors: Without these methods, you might forget to set the owning side (review.setProperty(property)), which can lead to inconsistent data.


// 1:
//mappedBy = "property":
//This specifies that the property field in the Review entity owns the relationship. Hibernate uses this field to understand the association.

// 2:
//        cascade = CascadeType.ALL:
//This ensures that all operations performed on a Property (e.g., persist, merge, remove) are cascaded to its associated Review entities. When a Property is deleted, its associated Review entities are also deleted.

// 3:
//        orphanRemoval = true:
//This ensures that if a Review is removed from the reviews list of a Property, the Review
// is also deleted from the database. This is particularly useful when you want to delete
// Review entities individually by manipulating the reviews list.



//                           mappedBy

//The mappedBy attribute specifies that the Room entity contains the foreign key (property)
// that maps back to this Property.

//                        mappedBy = "property"
//The mappedBy attribute refers to the field name in the Room entity that owns the
// relationship.
// This means the Room entity will have a field like:

       //@ManyToOne
       //@JoinColumn(name = "property_id")
       //private Property property;

// The Room entity holds the foreign key (property_id), and the Property entity is the
// inverse side of the relationship.

//                      cascade = CascadeType.ALL

// This setting enables cascading operations from the Property entity to its associated Room
// entities. Any operations performed on a Property (e.g., persist, merge, remove, etc.)
// will cascade to its Room entities. For example:
// If you save a Property, its Room entities will also be saved automatically.
// If you delete a Property, all associated Room entities will also be deleted.

// In JPA (Java Persistence API), the CascadeType defines how operations on a parent entity
// should propagate to its associated child entities. This is specified in the @OneToOne,
// @OneToMany, @ManyToOne, or @ManyToMany relationships using the cascade attribute. The
// primary purpose is to simplify database operations by cascading certain actions, like
// persisting or deleting, from the parent to the child.

//                  private List<Room> rooms = new ArrayList<>();

// This initializes the rooms list to avoid NullPointerException when trying to add or remove rooms.
// It ensures that the Property entity always has a manageable collection of Room entities.


//            Jackson annotations     -->      @JsonIgnore

// if we use this annotation @JsonIgnore --> then these fields:

//        "rooms": [],
//        "reviews": [],
//        "bookings": [],
//        "images": []

// will not come back in the POSTMAN

//{
//        "id": 13,
//        "name": "sunrise view Villa",
//        "numberOfGuests": 4,
//        "numberOfBeds": 4,
//        "numberOfBathrooms": 2,
//        "numberOfBedrooms": 2,
//        "country": {
//        "id": 2,
//        "name": "Srilanka"
//        },
//        "city": {
//        "id": 6,
//        "name": "Colombo",
//        "country": {
//        "id": 2,
//        "name": "Srilanka"
//        }
//        },
//        "rooms": [],
//        "reviews": [],
//        "bookings": [],
//        "images": []
//        }

// if we want specific response in POSTMAN then --> use DTO (Preferred for Larger Applications)
