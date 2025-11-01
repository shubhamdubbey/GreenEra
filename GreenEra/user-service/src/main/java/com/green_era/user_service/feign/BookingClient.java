package com.green_era.user_service.feign;

import com.green_era.user_service.dto.BookingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "booking-service")
public interface BookingClient {

    @GetMapping("/booking/getALlUsersBookings/{email}")
    List<BookingDto> getALlUsersBookings(@PathVariable("email") String email);

    @PatchMapping("/booking/cancelBooking/{id}")
    String cancelBooking(@PathVariable("id") Long id);
}
