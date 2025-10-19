package com.green_era.booking_service.controller;

import com.green_era.booking_service.dto.BookingDto;
import com.green_era.booking_service.service.BookingService;
import com.green_era.booking_service.utils.BookingNotFoundException;
import com.green_era.booking_service.utils.BookingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @PostMapping("newBooking")
    private ResponseEntity<BookingDto> createNewBooking(@RequestBody BookingDto dto){
        return new ResponseEntity<>(bookingService.createBooking(dto), HttpStatus.CREATED);
    }

    @GetMapping("bookings")
    private ResponseEntity<List<BookingDto>> getAllBookings(){
        return new ResponseEntity<>(bookingService.getAllBooking(), HttpStatus.OK);
    }

    @GetMapping("booking/{id}")
    private ResponseEntity<BookingDto> getBookingById(@PathVariable("id") Long id) throws BookingNotFoundException {
        return new ResponseEntity<>(bookingService.getBookingById(id), HttpStatus.OK);
    }

    @PutMapping("updateBooking/{id}")
    private ResponseEntity<BookingDto> updateBooking(@PathVariable("id") Long id, @RequestBody BookingDto dto) throws BookingNotFoundException {
        return new ResponseEntity<>(bookingService.updateBooking(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("deleteBooking/{id}")
    private ResponseEntity<String> deleteBooking(@PathVariable Long id) throws BookingNotFoundException {
        return new ResponseEntity<>(bookingService.deleteBooking(id), HttpStatus.OK);
    }

    @GetMapping("getAllUsersBookings{email}")
    private ResponseEntity<List<BookingDto>> getALlUsersBookings(@PathVariable("email") String email){
        return new ResponseEntity<>(bookingService.getBooingByUser(email), HttpStatus.OK);
    }

    @GetMapping("getAllGardenerBookings{email}")
    private ResponseEntity<List<BookingDto>> getAllGardenerBookings(@PathVariable("email") String email){
        return new ResponseEntity<>(bookingService.getBookingByGardener(email), HttpStatus.OK);
    }

    @PatchMapping("updateBookingStatus/{id}/{status}")
    private ResponseEntity<BookingDto> updateBookingStatus(@PathVariable("id") Long id, @PathVariable BookingStatus status) throws BookingNotFoundException {
        return new ResponseEntity<>(bookingService.updateStatus(id, status), HttpStatus.OK);
    }
}
