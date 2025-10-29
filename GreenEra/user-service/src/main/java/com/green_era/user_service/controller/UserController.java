package com.green_era.user_service.controller;

import com.green_era.user_service.dto.RegisterUserDto;
import com.green_era.user_service.dto.UserDto;
import com.green_era.user_service.service.UserService;
import com.green_era.user_service.utils.UserAlreadyExistException;
import com.green_era.user_service.utils.UserNotFoundException;
import org.apache.catalina.User;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserService userService;
    
   @PostMapping("register")
    ResponseEntity<UserDto> registerUser(@RequestBody RegisterUserDto userDto) throws UserAlreadyExistException {
        return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.OK);
    }

    @GetMapping("getUser/{id}")
    ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) throws UserNotFoundException {
       return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping("getUser/{email}")
    ResponseEntity<UserDto> getUser(@PathVariable("email") String email) throws UserNotFoundException {
        return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
    }

    @GetMapping("getAllUsers")
    ResponseEntity<List<UserDto>> getAllUser(){
       return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PutMapping("changeId/{id}")
    ResponseEntity<String> updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) throws UserNotFoundException {
        return new ResponseEntity<>(userService.updateUser(id, userDto), HttpStatus.OK);
    }

    @DeleteMapping("deleteUser/{id}")
    ResponseEntity<String> deleteById(@PathVariable("id") Long id) throws UserNotFoundException {
       return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
    }
}
