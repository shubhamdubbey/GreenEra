package com.green_era.booking_service.service;

import com.green_era.booking_service.dto.BookingDto;
import com.green_era.booking_service.entity.BookingEntity;
import com.green_era.booking_service.repository.BookingRepository;
import com.green_era.booking_service.utils.BookingNotFoundException;
import com.green_era.booking_service.utils.BookingStatus;
import com.green_era.booking_service.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService{

    @Autowired
    BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(BookingDto dto) {
        BookingEntity booking = Mapper.bookingDtoToBookingEntity(dto);
        bookingRepository.save(booking);
        return Mapper.bookingToBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long id) throws BookingNotFoundException {
        Optional<BookingEntity> bookingEntity = bookingRepository.findById(id);
        if(!bookingEntity.isPresent()) throw new BookingNotFoundException("Can't find booking with the given booking id.");
        return Mapper.bookingToBookingDto(bookingEntity.get());
    }

    @Override
    public List<BookingDto> getAllBooking() {
        List<BookingEntity> allBookings = bookingRepository.findAll();
        List<BookingDto> bookings = new ArrayList<>();
        allBookings.forEach(booking -> {
            bookings.add(Mapper.bookingToBookingDto(booking));
        });

        return bookings;
    }

    @Override
    public BookingDto updateBooking(Long id, BookingDto dto) throws BookingNotFoundException {
        Optional<BookingEntity> bookingEntity = bookingRepository.findById(id);
        if(!bookingEntity.isPresent()) throw new BookingNotFoundException("Can't find booking with the given booking id.");
        BookingEntity booking = Mapper.bookingDtoToBookingEntity(dto);
        bookingRepository.save(booking);

        return dto;
    }

    @Override
    public String cancelBooking(Long id) throws BookingNotFoundException {
        Optional<BookingEntity> bookingEntity = bookingRepository.findById(id);
        if(!bookingEntity.isPresent()) throw new BookingNotFoundException("Can't find booking with the given booking id.");
        BookingEntity booking = bookingEntity.get();
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return "Success";
    }

    @Override
    public List<BookingDto> getBooingByUser(String userEmail) {
        List<BookingEntity> allUserBooking = bookingRepository.findBookingByUserEmail(userEmail);
        List<BookingDto> bookings = new ArrayList<>();
        allUserBooking.forEach(booking -> {
            bookings.add(Mapper.bookingToBookingDto(booking));
        });

        return bookings;
    }

    @Override
    public List<BookingDto> getBookingByGardener(String gardenerEmail) {
        List<BookingEntity> allGardenerBooking = bookingRepository.findBookingByGardenerEmail(gardenerEmail);
        List<BookingDto> bookings = new ArrayList<>();

        allGardenerBooking.forEach(booking -> {
            bookings.add(Mapper.bookingToBookingDto(booking));
        });

        return bookings;
    }

    @Override
    public String completeBooking(Long id) throws BookingNotFoundException {
        Optional<BookingEntity> bookingEntity = bookingRepository.findById(id);
        if(!bookingEntity.isPresent()) throw new BookingNotFoundException("Can't find booking with the given booking id.");
        BookingEntity booking = bookingEntity.get();
        booking.setBookingStatus(BookingStatus.COMPLETED);
        // fiegn client
        bookingRepository.save(booking);

        return "Success";
    }
}
