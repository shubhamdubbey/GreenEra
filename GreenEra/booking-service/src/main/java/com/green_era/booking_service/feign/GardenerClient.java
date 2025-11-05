package com.green_era.booking_service.feign;

import com.green_era.booking_service.dto.GardenerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "gardener-service")
public interface GardenerClient {

    // ✅ Fetch a gardener by email (unique identifier)
    @GetMapping("/gardener/getGardener/{email}")
    GardenerDto getGardenerByEmail(@PathVariable("email") String email);

    // ✅ Fetch all gardeners (used for intelligent scheduling)
    @GetMapping("/gardener/getGardeners")
    List<GardenerDto> getAllGardeners();

    // ✅ Fetch gardeners based on locality, availability, and type (optional use)
    @GetMapping("/gardener/getAvailableGardeners/{locality}/{availability}/{gardenerType}")
    List<GardenerDto> getAvailableGardeners(@PathVariable("locality") String locality,
                                            @PathVariable("availability") boolean availability,
                                            @PathVariable("gardenerType") String gardenerType);

    // ✅ Update availability (after booking creation or cancellation)
    @PatchMapping("/gardener/updateAvailability/{email}/{availability}")
    GardenerDto updateAvailability(@PathVariable("email") String email,
                                   @PathVariable("availability") boolean availability);
}
