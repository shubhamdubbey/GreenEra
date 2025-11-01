package com.green_era.user_service.service;

import com.green_era.user_service.dto.BookingDto;
import com.green_era.user_service.dto.RegisterUserDto;
import com.green_era.user_service.dto.UserDto;
import com.green_era.user_service.utils.UserAlreadyExistException;
import com.green_era.user_service.utils.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserDto registerUser(RegisterUserDto userDto) throws UserAlreadyExistException;
    UserDto getUserById(Long id) throws UserNotFoundException;
    UserDto getUserByEmail(String email) throws UserNotFoundException;
    List<UserDto> getAllUsers();
    String deleteUser(Long id) throws UserNotFoundException;
    String updateUser(Long id, UserDto userDto) throws UserNotFoundException;
    List<BookingDto> getAllBookings(String email);
    String cancelBooking(Long id);
}
