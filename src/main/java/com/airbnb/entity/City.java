package com.airbnb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    // Many cities belong to one country
    // by doing this --> we can know --> which city belongs to which country
    // so supply country name in --> REQUEST
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false) // foreign key in city table
    private Country country;


}

