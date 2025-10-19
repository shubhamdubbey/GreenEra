package com.green_era.booking_service.utils;

public enum BookingStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled");

    String status;

    BookingStatus(String status) {this.status = status;}

    public String getStatus(){return status;}
}
