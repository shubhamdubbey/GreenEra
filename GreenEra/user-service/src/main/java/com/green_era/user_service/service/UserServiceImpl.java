package com.green_era.user_service.service;

import com.green_era.user_service.dto.RegisterUserDto;
import com.green_era.user_service.dto.UserDto;
import com.green_era.user_service.entity.UserEntity;
import com.green_era.user_service.repository.UserRepository;
import com.green_era.user_service.utils.Mapper;
import com.green_era.user_service.utils.UserAlreadyExistException;
import com.green_era.user_service.utils.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDto registerUser(RegisterUserDto userDto) throws UserAlreadyExistException {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if(optionalUser.isPresent()) throw new UserAlreadyExistException("User is already registered with give email id.");

        optionalUser = userRepository.findByPhoneNumber(userDto.getPhoneNumber());
        if(optionalUser.isPresent()) throw new UserAlreadyExistException("User is already registered with given phone number.");
        UserEntity user = Mapper.registerUserToUser(userDto);
        user.setCreatedAt(LocalDate.now());
        user.setUpdatedAt(LocalDate.now());

        userRepository.save(user);

        return Mapper.userToUserDto(user);
    }

    @Override
    public UserDto getUserById(Long id) throws UserNotFoundException {
        Optional<UserEntity> user = userRepository.findById(id);
        if(user.isPresent()) return Mapper.userToUserDto(user.get());
        else throw new UserNotFoundException("No user registered with the given mail id.");
    }

    @Override
    public UserDto getUserByEmail(String email) throws UserNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if(userEntity.isPresent()) return Mapper.userToUserDto(userEntity.get());
        else throw new UserNotFoundException("No user registered with the given mail id");
    }

    @Override
    public List<UserDto> getAllUsers() {
        Iterable<UserEntity> listOfUsers = userRepository.findAll();
        List<UserDto> allUsers = new ArrayList<>();
        listOfUsers.forEach(user -> {
            allUsers.add(Mapper.userToUserDto(user));
        });

        return allUsers;
    }

    @Override
    public String deleteUser(Long id) throws UserNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if(userEntity.isPresent()){
            UserEntity user = userEntity.get();
            user.setActive(false);
            userRepository.save(user);
            return "success";
        } else throw new UserNotFoundException("No user registered with the given mail id.");
    }

    @Override
    public String updateUser(Long id, UserDto userDto) throws UserNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if(userEntity.isPresent()){
            UserEntity user = userEntity.get();

            user.setFirstName(userDto.getFirstName());
            user.setEmail(userDto.getEmail());
            user.setLastName(userDto.getEmail());
            user.setPhoneNumber(userDto.getPhoneNumber());
            user.setUpdatedAt(LocalDate.now());

            userRepository.save(user);

            return "Success";
        }
        else throw new UserNotFoundException("No user registered with the given email id.");
    }
}
