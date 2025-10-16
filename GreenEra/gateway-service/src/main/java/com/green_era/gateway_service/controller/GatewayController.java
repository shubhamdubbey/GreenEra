package com.green_era.gateway_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway")
public class GatewayController {
    
    @GetMapping("welcome")
    public String welcome(){
        return "Welcome to Gateway Service";
    }
}
