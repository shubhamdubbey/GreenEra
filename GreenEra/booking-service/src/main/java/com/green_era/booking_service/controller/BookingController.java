package com.green_era.booking_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @GetMapping("welcome")
    public String welcome(){
        return "Welcome to Booking Service";
    }
}
