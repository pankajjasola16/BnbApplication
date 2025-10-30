package com.airbnb.controller;


import com.airbnb.entity.Room;
import com.airbnb.repository.RoomRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// GIVE PERMISSION TO THIS CONTROLLER IN --> SecurityConfig.java

@RestController
public class RoomController {

    private RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @PostMapping("/addRoom")
    public String addRoom(@RequestBody Room room) {
        roomRepository.save(room);

        return "added";
    }

    // below code is to get the type of the room, so that when I select the Hotel , its type will be autofill
    // http://localhost:8080/rooms/byProperty/2
    @GetMapping("/rooms/byProperty/{propertyId}")
    public List<String> getRoomTypesByProperty(@PathVariable Long propertyId) {
        return roomRepository.findByPropertyId(propertyId)
                .stream()
                .map(Room::getType)   // take only type
                .distinct()           // avoid duplicates
                .toList();
    }



}

