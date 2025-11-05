package com.green_era.booking_service.feign;

import com.green_era.booking_service.dto.GardenerAvaibilityDto;
import com.green_era.booking_service.dto.GardenerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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
    GardenerDto updateAvailability(@PathVariable("email") String email,
                                   @PathVariable("availability") boolean availability);

    @PostMapping("gardener/blockSlot")
    String blockSlot(@RequestBody GardenerAvaibilityDto dto);
}
