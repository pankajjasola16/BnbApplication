package com.airbnb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "description", length = 2500)
    private String description;

    // several reviews are link with one AppUser but remember one user can give review
    // only once for a one property so do coding for that in reviewController.java
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    // many reviews associated with one property
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

}