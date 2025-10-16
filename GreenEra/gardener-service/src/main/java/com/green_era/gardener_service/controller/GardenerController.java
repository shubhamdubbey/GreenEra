package com.green_era.gardener_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gardener")
public class GardenerController {
    
    @GetMapping("welcome")
    public String welcome(){
        return "Welcome to Gardener Service";
    }
}
