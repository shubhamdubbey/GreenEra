package com.green_era.gardener_service.controller;

import com.green_era.gardener_service.dto.BookingDto;
import com.green_era.gardener_service.dto.GardenerAvaibilityDto;
import com.green_era.gardener_service.dto.GardenerDto;
import com.green_era.gardener_service.service.GardenerService;
import com.green_era.gardener_service.utils.AccountNotFoundException;
import com.green_era.gardener_service.utils.DuplicateAccountException;
import com.green_era.gardener_service.utils.GardenerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/gardener")
public class GardenerController {

    @Autowired
    GardenerService gardenerService;
    
    @PostMapping("registerGardener")
    ResponseEntity<GardenerDto> registerGardener(@RequestBody GardenerDto dto) throws DuplicateAccountException {
        return new ResponseEntity<>(gardenerService.registerGardener(dto), HttpStatus.CREATED);
    }

    @GetMapping("getGardeners")
    ResponseEntity<List<GardenerDto>> getGardeners(){
        return new ResponseEntity<>(gardenerService.getAllGardeners(), HttpStatus.OK);
    }

    @GetMapping("getGardener/{id}")
    ResponseEntity<GardenerDto> getGardener(@PathVariable("id") Long id) throws AccountNotFoundException {
        return new ResponseEntity<>(gardenerService.getGardenerById(id), HttpStatus.OK);
    }

    @GetMapping("getGardener/{email}")
    ResponseEntity<GardenerDto> getGardenerByEmail(@PathVariable("email") String email) throws AccountNotFoundException {
        return new ResponseEntity<>(gardenerService.getGardenerByEmail(email), HttpStatus.OK);
    }

    @PatchMapping("markUnavailableByEmail/{email}")
    ResponseEntity<GardenerDto> markUnavailableByEmail(@PathVariable("email") String email) throws AccountNotFoundException {
        return new ResponseEntity<>(gardenerService.markUnavailableByEmail(email), HttpStatus.OK);
    }

    @PutMapping("updateGardener/{id}")
    ResponseEntity<String> updateGardener(@PathVariable("id") Long id, @RequestBody GardenerDto dto) throws AccountNotFoundException {
        return new ResponseEntity<>(gardenerService.updateGardener(id, dto), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("deleteGardener/{id}")
    ResponseEntity<String> deleteGardener(@PathVariable Long id) throws AccountNotFoundException {
        return new ResponseEntity<>(gardenerService.deleteGardener(id), HttpStatus.OK);
    }

    @GetMapping("getAvailableGardeners/{locality}/{availability}/{gardenerType}")
    ResponseEntity<List<GardenerDto>> getAvailableGardeners(@PathVariable String locality, @PathVariable boolean availability, @PathVariable GardenerType gardenerType){
        return new ResponseEntity<>(gardenerService.findByLocalityAndIsAvailableAndGardenerType(locality, availability, gardenerType), HttpStatus.OK);
    }

    @PatchMapping("updateAvailability/{email}/{availability}")
    ResponseEntity<GardenerDto> updateAvailability(@PathVariable("email") String email, @PathVariable("availability") boolean availability) throws AccountNotFoundException {
        return new ResponseEntity<>(gardenerService.updateAvailability(email, availability), HttpStatus.ACCEPTED);
    }

    @GetMapping("getAllGardenerBookings/{email}")
    ResponseEntity<List<BookingDto>> getAllBookings(@PathVariable("email") String email) throws AccountNotFoundException {
        return new ResponseEntity<>(gardenerService.getAllBookings(email), HttpStatus.ACCEPTED);
    }

    @PostMapping("blockSlot")
    ResponseEntity<String> blockSlot(@RequestBody GardenerAvaibilityDto dto){
        return new ResponseEntity<>(gardenerService.BlockGardenerSlot(dto), HttpStatus.OK);
    }

    @GetMapping("getBlockedSlots/{email}")
    ResponseEntity<List<GardenerAvaibilityDto>> getBlockedSlots(@PathVariable("email") String email){
        return new ResponseEntity<>(gardenerService.getBlockedSlots(email), HttpStatus.OK);
    }

    @DeleteMapping("deleteBlockedSlots/{email}/{date}/{time}")
    ResponseEntity<String> deleteBlockedSlots(@PathVariable("email") String email, @PathVariable("date")LocalDate date, @PathVariable("time")LocalTime time){
        return new ResponseEntity<>(gardenerService.deleteBlockedSlots(email, date, time), HttpStatus.OK);
    }

}
