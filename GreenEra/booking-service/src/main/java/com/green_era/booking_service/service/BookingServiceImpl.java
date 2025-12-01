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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.green_era.booking_service.utils.Utility.*;

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
    // CREATE BOOKING (Intelligent + Slot-Aware + Fair + Safe)
    // ------------------------------------------------------------------------
    @Override
    @Transactional
    public BookingDto createBooking(BookingDto dto) {
        // ------------------- 1. BASIC VALIDATIONS -------------------
        if (dto == null)
            throw new IllegalArgumentException("Booking request cannot be null.");

        if (dto.getBookingDate() == null)
            throw new IllegalArgumentException("Booking date is required.");

        if (dto.getStartTime() == null)
            throw new IllegalArgumentException("Start time is required.");

        if (dto.getBookingDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Cannot book a past date.");

        if (dto.getBookingType() == null)
            dto.setBookingType(BookingType.REGULAR);

        // Normalize to nearest 30-min slot
        dto.setStartTime(normalizeToSlot(dto.getStartTime()));
        dto.setEndTime(dto.getStartTime().plusMinutes(SLOT_DURATION_MINUTES));

        // ------------------- 2. FETCH GARDENERS -------------------
        List<GardenerDto> allGardeners;
        try {
            allGardeners = gardenerClient.getAllGardeners();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch gardeners. Gardener service may be unavailable.");
        }

        if (allGardeners == null || allGardeners.isEmpty())
            throw new EntityNotFoundException("No gardeners registered in the system.");

        // Filter by type (regular/urgent)
        List<GardenerDto> eligiblePool = filterGardenersByType(allGardeners, dto.getBookingType());

        // ------------------- 3. FILTER AVAILABLE & SLOT-FREE -------------------
        eligiblePool = eligiblePool.stream()
                .filter(g -> g.getAvailable()
                        && (g.getWorkStartTime() == null || g.getWorkStartTime().isBefore(dto.getStartTime()))
                        && (g.getWorkEndTime() == null || g.getWorkEndTime().isAfter(dto.getEndTime())))
                .filter(g -> !isSlotBlocked(g.getEmail(), dto))
                .collect(Collectors.toList());

        // ------------------- 4. FALLBACK (URGENT → REGULAR POOL) -------------------
        if (eligiblePool.isEmpty() && dto.getBookingType() == BookingType.URGENT) {
            eligiblePool = filterGardenersByType(allGardeners, BookingType.REGULAR).stream()
                    .filter(g -> g.getAvailable() && !isSlotBlocked(g.getEmail(), dto))
                    .collect(Collectors.toList());
        }

        if (eligiblePool.isEmpty())
            throw new IllegalStateException("No gardeners available for this slot. Try a different time.");

        // ------------------- 5. FAIRNESS: PICK LEAST BOOKED -------------------
        GardenerDto selected = eligiblePool.stream()
                .min(Comparator.comparingInt(Utility::getBookingCountForGardener))
                .orElseThrow(() -> new IllegalStateException("Unable to assign gardener."));

        // ------------------- 6. FETCH USER DETAILS -------------------
        UserDto user;
        try {
            user = userClient.getUser(dto.getUserEmail());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch user info. User service may be down.");
        }

        if (user == null)
            throw new EntityNotFoundException("User not found for email: " + dto.getUserEmail());

        // ------------------- 7. PRICE CALCULATION -------------------
        double price = calculatePrice(dto, selected.getHourlyRate());

        // ------------------- 8. SAVE BOOKING -------------------
        BookingEntity booking = Mapper.bookingDtoToBookingEntity(dto);
        booking.setGardenerEmail(selected.getEmail());
        booking.setGardenerName(selected.getName());
        booking.setGardenerPhone(selected.getPhoneNumber());
        booking.setUserName(user.getFirstName() + " " + user.getLastName());
        booking.setUserPhone(user.getPhoneNumber());
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPrice(price);

        BookingEntity saved;
        try {
            saved = bookingRepository.save(booking);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Booking conflict detected. Please retry after a few seconds.");
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected error while saving booking: " + e.getMessage());
        }

        // ------------------- 9. UPDATE AVAILABILITY + BLOCK SLOT -------------------
        try {
            gardenerClient.updateAvailability(selected.getEmail(), false);

            GardenerAvaibilityDto slotDto = new GardenerAvaibilityDto(
                    selected.getEmail(),
                    booking.getBookingDate(),
                    booking.getStartTime(),
                    booking.getEndTime()
            );
            gardenerClient.blockSlot(slotDto);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to update gardener slot. Booking rolled back.");
        }

        // ------------------- 10. RETURN -------------------
        return Mapper.bookingToBookingDto(saved);
    }

    @Override
    public BookingDto getBookingById(Long id) throws BookingNotFoundException {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("No booking found with ID " + id));
        return Mapper.bookingToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBooking() {
        return bookingRepository.findAll().stream()
                .map(Mapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto updateBooking(Long id, BookingDto dto) throws BookingNotFoundException {
        BookingEntity existing = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found."));
        BookingEntity updated = Mapper.bookingDtoToBookingEntity(dto);
        updated.setId(existing.getId());
        bookingRepository.save(updated);
        return dto;
    }

    @Override
    @Transactional
    public String cancelBooking(Long id) throws BookingNotFoundException {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found."));
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        try {
            gardenerClient.deleteBookingSlot(booking.getGardenerEmail(), booking.getBookingDate(), booking.getStartTime());
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Failed to free slot for cancelled booking.");
        }

        return "Cancelled successfully.";
    }

    @Override
    @Transactional
    public String completeBooking(Long id) throws BookingNotFoundException {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found."));
        booking.setBookingStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        try {
            gardenerClient.deleteBookingSlot(booking.getGardenerEmail(), booking.getBookingDate(), booking.getStartTime());
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Failed to free slot for completed booking.");
        }

        return "Completed successfully.";
    }

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
}
