package com.green_era.booking_service.feign;

import com.green_era.booking_service.dto.GardenerAvaibilityDto;
import com.green_era.booking_service.dto.GardenerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@FeignClient(name = "gardener-service")
public interface GardenerClient {

    @GetMapping("/gardener/getGardener/{email}")
    GardenerDto getGardenerByEmail(@PathVariable("email") String email);


    @GetMapping("/gardener/getGardeners")
    List<GardenerDto> getAllGardeners();


    @GetMapping("/gardener/getAvailableGardeners/{locality}/{availability}/{gardenerType}")
    List<GardenerDto> getAvailableGardeners(@PathVariable("locality") String locality,
                                            @PathVariable("availability") boolean availability,
                                            @PathVariable("gardenerType") String gardenerType);


    @PatchMapping("/gardener/updateAvailability/{email}/{availability}")
    void updateAvailability(@PathVariable("email") String email,
                                   @PathVariable("availability") boolean availability);

    @PostMapping("gardener/blockSlot")
    void blockSlot(@RequestBody GardenerAvaibilityDto dto);

    @GetMapping("/gardener/getBlockedSlots/{email}/{date}")
    List<GardenerAvaibilityDto> getBlockedSlots(@PathVariable("email") String email, @PathVariable("date") LocalDate date);


    @DeleteMapping("/gardener/deleteBlockedSlots/{email}/{date}/{time}")
    void deleteBookingSlot(@PathVariable("email") String email,
                     @PathVariable("date") LocalDate date,
                     @PathVariable("time") LocalTime time);
}
