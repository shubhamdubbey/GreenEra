package com.green_era.booking_service.service;

import com.green_era.booking_service.dto.BookingDto;
import com.green_era.booking_service.dto.GardenerDto;
import com.green_era.booking_service.entity.BookingEntity;
import com.green_era.booking_service.feign.GardenerClient;
import com.green_era.booking_service.repository.BookingRepository;
import com.green_era.booking_service.utils.BookingNotFoundException;
import com.green_era.booking_service.utils.BookingStatus;
import com.green_era.booking_service.utils.Mapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.green_era.booking_service.utils.Utility.calculatePrice;

@Service
public class BookingServiceImpl implements BookingService{

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    GardenerClient gardenerClient;

    @Override
    public BookingDto createBooking(BookingDto dto) {
        if (dto.getBookingDate() == null) throw new IllegalArgumentException("Booking date required");
        if (dto.getStartTime() == null || dto.getEndTime() == null) throw new IllegalArgumentException("Start and end times required");
        if (!dto.getEndTime().isAfter(dto.getStartTime())) throw new IllegalArgumentException("endTime must be after startTime");

        // Check gardener exists & availability
        if (dto.getGardenerEmail() == null || dto.getGardenerEmail().isEmpty()) {
            throw new IllegalArgumentException("gardenerEmail required");
        }
        GardenerDto gardener = gardenerClient.getGardenerByEmail(dto.getGardenerEmail());
        if (gardener.getId() == 0) {
            throw new EntityNotFoundException("Gardener not found with given email id.");
        }
        if (!gardener.getAvailable()) {
            throw new IllegalStateException("Gardener not available in this time frame.");
        }


        // Calculate price sample: duration(hours) * hourlyRate, urgent -> *1.5
        double price = calculatePrice(dto, gardener.getHourlyRate());

        BookingEntity booking = Mapper.bookingDtoToBookingEntity(dto);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setPrice(price);

        BookingEntity saved = bookingRepository.save(booking);

        // Mark gardener unavailable (simple approach; can be improved to per-time-slot)
        gardener.setAvailable(false);
        gardenerClient.updateAvailability(gardener.getId(), false);

        return Mapper.bookingToBookingDto(saved);
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
        bookingRepository.save(booking);

        return "Success";
    }
}
