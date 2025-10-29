package com.green_era.user_service.utils;


import com.green_era.user_service.dto.RegisterUserDto;
import com.green_era.user_service.dto.UserDto;
import com.green_era.user_service.entity.UserEntity;

public class Mapper {

    public static UserEntity registerUserToUser(RegisterUserDto userDto){
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        userEntity.setRole(userDto.getRole());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPassword(userDto.getPassword());
        userEntity.setPhoneNumber(userDto.getPhoneNumber());
        userEntity.setAddress(userDto.getAddress());

        return userEntity;
    }

    public static UserDto userToUserDto(UserEntity user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setFirstName(user.getFirstName());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setLastName(user.getLastName());
        userDto.setId(user.getId());
        userDto.setActive(user.getActive());
        userDto.setAddress(user.getAddress());
        return userDto;
    }
}
