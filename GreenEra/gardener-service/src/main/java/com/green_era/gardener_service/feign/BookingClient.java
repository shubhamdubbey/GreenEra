package com.green_era.gardener_service.feign;

import com.green_era.gardener_service.dto.BookingDto;
import com.green_era.gardener_service.dto.GardenerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "booking-service")
public interface BookingClient {
    @GetMapping("/booking/getAllGardenerBookings/{email}")
    List<BookingDto> getBookingsbyGardener(@PathVariable("email") String email);
}
