package com.airbnb.repository;

import com.airbnb.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

//    @Query("SELECT r FROM Room r WHERE r.property.id = :propertyId AND r.type = :roomType")
//    Room findByPropertyIdAndType(@Param("propertyId") Long propertyId, @Param("roomType") String roomType);

    // r.property.id --> means r has property object --> and that property object has an --> id
    // here property is an object address
    // means I do not want property object --> I want to search property id --> so where is the
    // property id --> inside --> Property class --> private Property property; --> in Room.java

    // r.property.id --> inside the room --> there is property object --> inside property object -->
    // there is --> id

    @Query("SELECT r FROM Room r WHERE r.property.id = :propertyId AND r.type = :type AND r.date = :date")
    // below it will return --> room object not List--> List<Room> ---> because I have a single
        // entry
    Room findByPropertyIdAndTypeAndDate(
            @Param("propertyId") Long propertyId,
            @Param("type") String type,
            @Param("date") LocalDate date);
}