package com.airbnb.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {

    private Long id;
    private Integer rating;
    private String description;

    // no need
//    private Long userId;        // Foreign key to the user table
//    private Long propertyId;    // Foreign key to the property table
}
