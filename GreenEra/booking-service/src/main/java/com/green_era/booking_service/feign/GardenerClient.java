package com.green_era.booking_service.feign;

import com.green_era.booking_service.dto.GardenerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "gardener-service")
public interface GardenerClient {

    @GetMapping("/gardener/getGardener/{email}")
    GardenerDto getGardenerByEmail(@PathVariable("email") String email);

    @GetMapping("/gardener/getAvailableGardeners/{locality}/{availability}")
    List<GardenerDto> getAvailableGardeners(@PathVariable("locality") String locality,
                                            @PathVariable("availability") boolean availability);

    @PatchMapping("/gardener/updateAvailability/{email}/{availability}")
    GardenerDto updateAvailability(@PathVariable("email") String email,
                                   @PathVariable("availability") boolean availability);
}
