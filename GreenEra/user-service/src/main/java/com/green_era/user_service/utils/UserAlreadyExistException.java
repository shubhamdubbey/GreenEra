package com.green_era.user_service.utils;

public class UserAlreadyExistException extends Exception{
    public UserAlreadyExistException(String message) {super(message);}
}
