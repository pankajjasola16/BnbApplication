package com.airbnb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "count", nullable = false)
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Version
    private long version;

// @Version --> it comes from Spring Boot --> above code is for multiple transaction
    // and version number will go automatically in the table

    // when we book the room we the room with version number

    // when I save a record --> version number will be given --> And now when you read the record,
    // The record goes to the same version to --> 10 people --> and when any one person updates
    // the record --> the version number is changed to 2 --> while the other people will not be
    // able to update --> so in that way it can handle the multiple transaction (Optimistic)

// Many rooms are in one property
// we can also keep the --> type in another table by doing Normalization

}






