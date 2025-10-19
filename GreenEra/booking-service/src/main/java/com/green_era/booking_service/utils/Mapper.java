package com.green_era.booking_service.utils;

import com.green_era.booking_service.dto.BookingDto;
import com.green_era.booking_service.entity.BookingEntity;

public class Mapper {
    public static BookingDto bookingToBookingDto(BookingEntity booking){
        BookingDto bookingDto = new BookingDto();
        bookingDto.setBookingDate(booking.getBookingDate());
        bookingDto.setBookingType(booking.getBookingType());
        bookingDto.setEndTime(booking.getEndTime());
        bookingDto.setUserEmail(booking.getUserEmail());
        bookingDto.setStartTime(booking.getStartTime());
        bookingDto.setGardenerEmail(booking.getGardenerEmail());
        bookingDto.setBookingStatus(booking.getBookingStatus());
        bookingDto.setId(bookingDto.getId());
        return bookingDto;
    }

    public static BookingEntity bookingDtoToBookingEntity(BookingDto dto){
        BookingEntity booking = new BookingEntity();

        booking.setBookingDate(dto.getBookingDate());
        booking.setBookingStatus(dto.getBookingStatus());
        booking.setEndTime(dto.getEndTime());
        booking.setBookingType(dto.getBookingType());
        booking.setStartTime(dto.getStartTime());
        booking.setGardenerEmail(dto.getGardenerEmail());
        booking.setUserEmail(dto.getUserEmail());
        if(dto.getId() != 0) booking.setId(dto.getId());

        return booking;
    }
}
