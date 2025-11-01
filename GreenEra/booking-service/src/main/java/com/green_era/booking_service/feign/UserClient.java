package com.green_era.booking_service.feign;

import com.green_era.booking_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/user/getUserByEmail/{email}")
    UserDto getUser(@PathVariable("email") String email);
}
