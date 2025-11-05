package com.green_era.booking_service.utils;

import com.green_era.booking_service.dto.BookingDto;
import com.green_era.booking_service.dto.GardenerDto;
import com.green_era.booking_service.entity.BookingEntity;
import com.green_era.booking_service.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class Utility {

    @Autowired
    private static BookingRepository bookingRepository;

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

    public static LocalTime normalizeToSlot(LocalTime time) {
        int minute = time.getMinute() < 30 ? 0 : 30;
        return LocalTime.of(time.getHour(), minute);
    }

    public static List<GardenerDto> filterGardenersByType(List<GardenerDto> all, BookingType type) {
        if (type == BookingType.URGENT)
            return all.stream().filter(g -> "URGENT".equalsIgnoreCase(g.getGardenerType()) || "BOTH".equalsIgnoreCase(g.getGardenerType())).collect(Collectors.toList());
        else
            return all.stream().filter(g -> "REGULAR".equalsIgnoreCase(g.getGardenerType()) || "BOTH".equalsIgnoreCase(g.getGardenerType())).collect(Collectors.toList());
    }

    public static boolean isGardenerBusy(String gardenerEmail, BookingDto dto) {
        List<BookingEntity> bookings = bookingRepository.findBookingByGardenerEmail(gardenerEmail);
        return bookings.stream().anyMatch(b ->
                b.getBookingDate().equals(dto.getBookingDate()) &&
                        !(b.getEndTime().isBefore(dto.getStartTime()) || b.getStartTime().isAfter(dto.getEndTime()))
        );
    }

    public static int getBookingCountForGardener(GardenerDto gardener) {
        return bookingRepository.findBookingByGardenerEmail(gardener.getEmail()).size();
    }
}
