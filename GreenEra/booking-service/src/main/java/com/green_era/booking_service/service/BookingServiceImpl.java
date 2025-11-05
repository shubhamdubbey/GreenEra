package com.green_era.booking_service.service;

import com.green_era.booking_service.dto.BookingDto;
import com.green_era.booking_service.dto.GardenerAvaibilityDto;
import com.green_era.booking_service.dto.GardenerDto;
import com.green_era.booking_service.dto.UserDto;
import com.green_era.booking_service.entity.BookingEntity;
import com.green_era.booking_service.feign.GardenerClient;
import com.green_era.booking_service.feign.UserClient;
import com.green_era.booking_service.repository.BookingRepository;
import com.green_era.booking_service.utils.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.green_era.booking_service.utils.Utility.calculatePrice;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private GardenerClient gardenerClient;

    private static final int SLOT_DURATION_MINUTES = 30;

    // ------------------------------------------------------------------------
    // CREATE BOOKING (With Intelligent Assignment)
    // ------------------------------------------------------------------------
    @Override
    @Transactional
    public BookingDto createBooking(BookingDto dto) {
        if (dto.getBookingDate() == null)
            throw new IllegalArgumentException("Booking date is required");

        if (dto.getBookingType() == null)
            dto.setBookingType(BookingType.REGULAR);

        // Normalize startTime to 30-min slot
        dto.setStartTime(normalizeToSlot(dto.getStartTime()));
        dto.setEndTime(dto.getStartTime().plusMinutes(SLOT_DURATION_MINUTES));

        // Step 1: Fetch all gardeners of required type
        List<GardenerDto> candidateGardeners = gardenerClient.getAllGardeners();
        if (candidateGardeners == null || candidateGardeners.isEmpty())
            throw new EntityNotFoundException("No gardeners available at the moment");

        List<GardenerDto> eligiblePool = filterGardenersByType(candidateGardeners, dto.getBookingType());

        // Step 2: Filter by availability and working hours
        eligiblePool = eligiblePool.stream()
                .filter(g -> g.getAvailable()
                        && (g.getWorkStartTime() == null || g.getWorkStartTime().isBefore(dto.getStartTime()))
                        && (g.getWorkEndTime() == null || g.getWorkEndTime().isAfter(dto.getEndTime())))
                .collect(Collectors.toList());

        // Step 3: Remove those already booked in that time slot
        eligiblePool = eligiblePool.stream()
                .filter(g -> !isGardenerBusy(g.getEmail(), dto))
                .collect(Collectors.toList());

        // Step 4: If URGENT and no urgent gardener available, fallback to REGULAR pool
        if (eligiblePool.isEmpty() && dto.getBookingType() == BookingType.URGENT) {
            eligiblePool = filterGardenersByType(candidateGardeners, BookingType.REGULAR);
            eligiblePool = eligiblePool.stream()
                    .filter(g -> g.getAvailable() && !isGardenerBusy(g.getEmail(), dto))
                    .collect(Collectors.toList());
        }

        if (eligiblePool.isEmpty())
            throw new IllegalStateException("No gardeners available for this slot");

        // Step 5: Choose gardener with least bookings for fairness
        GardenerDto selected = eligiblePool.stream()
                .min(Comparator.comparingInt(this::getBookingCountForGardener))
                .orElseThrow(() -> new IllegalStateException("Unable to assign gardener"));

        // Step 6: Fetch user details
        UserDto user = userClient.getUser(dto.getUserEmail());

        // Step 7: Compute price dynamically
        double price = calculatePrice(dto, selected.getHourlyRate());

        // Step 8: Save booking
        BookingEntity booking = Mapper.bookingDtoToBookingEntity(dto);
        booking.setGardenerEmail(selected.getEmail());
        booking.setGardenerName(selected.getName());
        booking.setGardenerPhone(selected.getPhoneNumber());
        booking.setUserName(user.getFirstName() + " " + user.getLastName());
        booking.setUserPhone(user.getPhoneNumber());
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPrice(price);

        BookingEntity saved = bookingRepository.save(booking);

        // Step 9: Mark gardener unavailable (temporary)
        gardenerClient.updateAvailability(selected.getEmail(), false);

        // Step 10: Block gardener's slot
        GardenerAvaibilityDto gardenerAvaibilityDto = new GardenerAvaibilityDto(selected.getEmail(), booking.getBookingDate(), booking.getStartTime(), booking.getEndTime());
        gardenerClient.blockSlot(gardenerAvaibilityDto);

        return Mapper.bookingToBookingDto(saved);
    }

    // ------------------------------------------------------------------------
    // GET BOOKING BY ID
    // ------------------------------------------------------------------------
    @Override
    public BookingDto getBookingById(Long id) throws BookingNotFoundException {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("No booking found with ID " + id));
        return Mapper.bookingToBookingDto(booking);
    }

    // ------------------------------------------------------------------------
    // GET ALL BOOKINGS
    // ------------------------------------------------------------------------
    @Override
    public List<BookingDto> getAllBooking() {
        return bookingRepository.findAll().stream()
                .map(Mapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------------
    // UPDATE BOOKING
    // ------------------------------------------------------------------------
    @Override
    public BookingDto updateBooking(Long id, BookingDto dto) throws BookingNotFoundException {
        BookingEntity existing = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        BookingEntity updated = Mapper.bookingDtoToBookingEntity(dto);
        updated.setId(existing.getId());
        bookingRepository.save(updated);
        return dto;
    }

    // ------------------------------------------------------------------------
    // CANCEL BOOKING
    // ------------------------------------------------------------------------
    @Override
    @Transactional
    public String cancelBooking(Long id) throws BookingNotFoundException {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        gardenerClient.updateAvailability(booking.getGardenerEmail(), true);
        return "Cancelled successfully";
    }

    // ------------------------------------------------------------------------
    // COMPLETE BOOKING
    // ------------------------------------------------------------------------
    @Override
    @Transactional
    public String completeBooking(Long id) throws BookingNotFoundException {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        booking.setBookingStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        gardenerClient.updateAvailability(booking.getGardenerEmail(), true);
        return "Completed successfully";
    }

    // ------------------------------------------------------------------------
    // BOOKINGS BY USER OR GARDENER
    // ------------------------------------------------------------------------
    @Override
    public List<BookingDto> getBookingByUser(String userEmail) {
        return bookingRepository.findBookingByUserEmail(userEmail).stream()
                .map(Mapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingByGardener(String gardenerEmail) {
        return bookingRepository.findBookingByGardenerEmail(gardenerEmail).stream()
                .map(Mapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------------
    // UTILITY METHODS
    // ------------------------------------------------------------------------
    private LocalTime normalizeToSlot(LocalTime time) {
        int minute = time.getMinute() < 30 ? 0 : 30;
        return LocalTime.of(time.getHour(), minute);
    }

    private List<GardenerDto> filterGardenersByType(List<GardenerDto> all, BookingType type) {
        if (type == BookingType.URGENT)
            return all.stream().filter(g -> "URGENT".equalsIgnoreCase(g.getGardenerType()) || "BOTH".equalsIgnoreCase(g.getGardenerType())).collect(Collectors.toList());
        else
            return all.stream().filter(g -> "REGULAR".equalsIgnoreCase(g.getGardenerType()) || "BOTH".equalsIgnoreCase(g.getGardenerType())).collect(Collectors.toList());
    }

    private boolean isGardenerBusy(String gardenerEmail, BookingDto dto) {
        List<BookingEntity> bookings = bookingRepository.findBookingByGardenerEmail(gardenerEmail);
        return bookings.stream().anyMatch(b ->
                b.getBookingDate().equals(dto.getBookingDate()) &&
                        !(b.getEndTime().isBefore(dto.getStartTime()) || b.getStartTime().isAfter(dto.getEndTime()))
        );
    }

    private int getBookingCountForGardener(GardenerDto gardener) {
        return bookingRepository.findBookingByGardenerEmail(gardener.getEmail()).size();
    }
}
