package com.green_era.booking_service.utils;

import com.green_era.booking_service.dto.BookingDto;

import java.time.Duration;

public class Utility {

    public static double calculatePrice(BookingDto dto, Double hourlyRate) {
        if (hourlyRate == null) hourlyRate = 200.0; // default
        Duration duration = Duration.between(dto.getStartTime(), dto.getEndTime());
        double hours = duration.toMinutes() / 60.0;
        double base = hours * hourlyRate;
        if (dto.getBookingType() == BookingType.URGENT) {
            base *= 1.5;
        }
        return Math.round(base * 100.0) / 100.0;
    }
}
