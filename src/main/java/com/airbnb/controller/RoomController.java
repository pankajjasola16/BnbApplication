package com.airbnb.controller;


import com.airbnb.entity.Room;
import com.airbnb.repository.RoomRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
