package com.green_era.booking_service.utils;

import com.green_era.booking_service.dto.BookingDto;
import com.green_era.booking_service.dto.GardenerAvaibilityDto;
import com.green_era.booking_service.dto.GardenerDto;
import com.green_era.booking_service.entity.BookingEntity;
import com.green_era.booking_service.feign.GardenerClient;
import com.green_era.booking_service.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class Utility {

    @Autowired
    private static BookingRepository bookingRepository;

    @Autowired
    static GardenerClient gardenerClient;

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

    public static int getBookingCountForGardener(GardenerDto gardener) {
        return bookingRepository.findBookingByGardenerEmail(gardener.getEmail()).size();
    }

    public static LocalTime normalizeToSlot(LocalTime time) {
        int minute = time.getMinute() < 30 ? 0 : 30;
        return LocalTime.of(time.getHour(), minute);
    }

    public static List<GardenerDto> filterGardenersByType(List<GardenerDto> all, BookingType type) {
        if (type == BookingType.URGENT)
            return all.stream().filter(g ->
                            "URGENT".equalsIgnoreCase(g.getGardenerType()) || "BOTH".equalsIgnoreCase(g.getGardenerType()))
                    .collect(Collectors.toList());
        else
            return all.stream().filter(g ->
                            "REGULAR".equalsIgnoreCase(g.getGardenerType()) || "BOTH".equalsIgnoreCase(g.getGardenerType()))
                    .collect(Collectors.toList());
    }

    public static boolean isSlotBlocked(String gardenerEmail, BookingDto dto) {
        try {
            List<GardenerAvaibilityDto> blockedSlots =
                    gardenerClient.getBlockedSlots(gardenerEmail, dto.getBookingDate());
            if (blockedSlots == null || blockedSlots.isEmpty())
                return false;

            return blockedSlots.stream().anyMatch(slot ->
                    !(slot.getEndTime().isBefore(dto.getStartTime()) ||
                            slot.getStartTime().isAfter(dto.getEndTime())));
        } catch (Exception e) {
            System.err.println("⚠️ Could not fetch blocked slots for gardener " + gardenerEmail + ": " + e.getMessage());
            // Fail-safe: assume slot busy
            return true;
        }
    }
}
