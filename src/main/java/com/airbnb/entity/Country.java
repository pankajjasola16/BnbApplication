package com.airbnb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

}

// Lecture - 23 --> JAVA-Pankaj-Sir-Feb-2
// Add data manually --> but better to create Country Controller --> in that the RestApi
// and that will be called as --> AddController --> then create JSON --> Take data and
// then create
// Parent table  It does not have the foreign key
// Child table  It has the foreign key
// Parent  country    AND    Child    property