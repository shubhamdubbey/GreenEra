package com.green_era.booking_service.utils;

public enum BookingType {
    REGULAR("Regular"),
    URGENT("Urgent");

    String type;

    BookingType(String type) {this.type = type;}

    public String getType() {return type;}
}
