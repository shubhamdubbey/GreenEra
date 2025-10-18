package com.green_era.gardener_service.controller;

import com.green_era.gardener_service.dto.GardenerDto;
import com.green_era.gardener_service.service.GardenerService;
import com.green_era.gardener_service.utils.AccountNotFoundException;
import com.green_era.gardener_service.utils.DuplicateAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("updateGardener/{id}")
    ResponseEntity<String> updateGardener(@PathVariable("id") Long id, @RequestBody GardenerDto dto) throws AccountNotFoundException {
        return new ResponseEntity<>(gardenerService.updateGardener(id, dto), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("deleteGardener/{id}")
    ResponseEntity<String> deleteGardener(@PathVariable Long id) throws AccountNotFoundException {
        return new ResponseEntity<>(gardenerService.deleteGardener(id), HttpStatus.OK);
    }

    @GetMapping("getAvailableGardeners/{locality}/{availability)")
    ResponseEntity<List<GardenerDto>> getAvailableGardeners(@PathVariable String locality, @PathVariable boolean availability){
        return new ResponseEntity<>(gardenerService.getAvailableGardeners(locality, availability), HttpStatus.OK);
    }

    @PatchMapping("updateAvailability/{id}/{availability}")
    ResponseEntity<GardenerDto> updateAvailability(@PathVariable("id") Long id, @PathVariable("availability") boolean availability) throws AccountNotFoundException {
        return new ResponseEntity<>(gardenerService.updateAvailability(id, availability), HttpStatus.ACCEPTED);
    }
}
