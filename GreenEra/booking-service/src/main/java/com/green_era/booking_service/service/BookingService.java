package com.green_era.booking_service.service;

import com.green_era.booking_service.dto.BookingDto;
import com.green_era.booking_service.utils.BookingNotFoundException;
import com.green_era.booking_service.utils.BookingStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookingService {
    BookingDto createBooking(BookingDto dto);
    BookingDto getBookingById(Long id) throws BookingNotFoundException;
    List<BookingDto> getAllBooking();
    BookingDto updateBooking(Long id, BookingDto dto) throws BookingNotFoundException;
    String deleteBooking(Long id) throws BookingNotFoundException;
    List<BookingDto> getBooingByUser(String userEmail);
    List<BookingDto> getBookingByGardener(String gardenerEmail);
    BookingDto updateStatus(Long id, BookingStatus status) throws BookingNotFoundException;
}
