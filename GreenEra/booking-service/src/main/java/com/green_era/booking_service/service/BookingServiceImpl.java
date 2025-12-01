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

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

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
    // CREATE BOOKING  (Resilience Added)
    // ------------------------------------------------------------------------
    @Override
    @Transactional
    public BookingDto createBooking(BookingDto dto) {

        // ---------- 1. VALIDATIONS ----------
        if (dto == null) throw new IllegalArgumentException("Booking request cannot be null.");
        if (dto.getBookingDate() == null) throw new IllegalArgumentException("Booking date is required.");
        if (dto.getStartTime() == null) throw new IllegalArgumentException("Start time is required.");
        if (dto.getBookingDate().isBefore(LocalDate.now())) throw new IllegalArgumentException("Cannot book a past date.");
        if (dto.getBookingType() == null) dto.setBookingType(BookingType.REGULAR);

        dto.setStartTime(normalizeToSlot(dto.getStartTime()));
        dto.setEndTime(dto.getStartTime().plusMinutes(SLOT_DURATION_MINUTES));

        // ---------- 2. FETCH GARDENERS (WITH RESILIENCE) ----------
        List<GardenerDto> allGardeners = getAllGardenersSafe();
        if (allGardeners == null || allGardeners.isEmpty())
            throw new IllegalStateException("Gardener service unavailable or no gardeners found.");

        List<GardenerDto> eligiblePool = filterGardenersByType(allGardeners, dto.getBookingType());

        // ---------- 3. AVAILABILITY & SLOT FILTERING ----------
        eligiblePool = eligiblePool.stream()
                .filter(g -> g.getAvailable()
                        && (g.getWorkStartTime() == null || g.getWorkStartTime().isBefore(dto.getStartTime()))
                        && (g.getWorkEndTime() == null || g.getWorkEndTime().isAfter(dto.getEndTime())))
                .filter(g -> !isSlotBlocked(g.getEmail(), dto))
                .collect(Collectors.toList());

        // ---------- 4. URGENT FALLBACK ----------
        if (eligiblePool.isEmpty() && dto.getBookingType() == BookingType.URGENT) {
            eligiblePool = filterGardenersByType(allGardeners, BookingType.REGULAR).stream()
                    .filter(g -> g.getAvailable() && !isSlotBlocked(g.getEmail(), dto))
                    .collect(Collectors.toList());
        }

        if (eligiblePool.isEmpty())
            throw new IllegalStateException("No gardeners available for this slot.");

        // ---------- 5. FAIRNESS ----------
        GardenerDto selected = eligiblePool.stream()
                .min(Comparator.comparingInt(Utility::getBookingCountForGardener))
                .orElseThrow(() -> new IllegalStateException("Could not select gardener."));

        // ---------- 6. FETCH USER (WITH RESILIENCE) ----------
        UserDto user = getUserSafe(dto.getUserEmail());
        if (user == null) throw new EntityNotFoundException("User not found: " + dto.getUserEmail());

        // ---------- 7. PRICE ----------
        double price = calculatePrice(dto, selected.getHourlyRate());

        // ---------- 8. SAVE BOOKING ----------
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
            throw new IllegalStateException("Conflict detected. Retry after few seconds.");
        }

        // ---------- 9. UPDATE GARDENER (WITH RESILIENCE) ----------
        updateAvailabilitySafe(selected.getEmail(), false);
        GardenerAvaibilityDto slotDto = new GardenerAvaibilityDto(
                selected.getEmail(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime()
        );
        blockSlotSafe(slotDto);

        // ---------- 10. RETURN ----------
        return Mapper.bookingToBookingDto(saved);
    }

    // ------------------------------------------------------------------------
    // SAFE EXTERNAL CALLS (RESILIENCE)
    // ------------------------------------------------------------------------

    @CircuitBreaker(name = "gardenerService", fallbackMethod = "getAllGardenersFallback")
    @Retry(name = "gardenerService")
    @TimeLimiter(name = "gardenerService")
    public List<GardenerDto> getAllGardenersSafe() {
        return gardenerClient.getAllGardeners();
    }

    public List<GardenerDto> getAllGardenersFallback(Throwable e) {
        System.out.println("⚠ Gardener service DOWN → Empty list provided.");
        return Collections.emptyList();
    }


    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    @Retry(name = "userService")
    @TimeLimiter(name = "userService")
    public UserDto getUserSafe(String email) {
        return userClient.getUser(email);
    }

    public UserDto getUserFallback(String email, Throwable e) {
        System.out.println("⚠ User service DOWN → Temporary fallback user.");
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setFirstName("Unknown");
        dto.setLastName("User");
        dto.setPhoneNumber("N/A");
        return dto;
    }


    @CircuitBreaker(name = "gardenerService", fallbackMethod = "availabilityFallback")
    @Retry(name = "gardenerService")
    @TimeLimiter(name = "gardenerService")
    public void updateAvailabilitySafe(String email, boolean available) {
        gardenerClient.updateAvailability(email, available);
    }

    public void availabilityFallback(String email, boolean available, Throwable e) {
        System.out.println("⚠ FAILED updating availability for " + email);
    }


    @CircuitBreaker(name = "gardenerService", fallbackMethod = "blockSlotFallback")
    @Retry(name = "gardenerService")
    @TimeLimiter(name = "gardenerService")
    public void blockSlotSafe(GardenerAvaibilityDto dto) {
        gardenerClient.blockSlot(dto);
    }

    public void blockSlotFallback(GardenerAvaibilityDto dto, Throwable e) {
        System.out.println("⚠ FAILED to block slot (fallback executed).");
    }


    // ------------------------------------------------------------------------
    // OTHER METHODS (NO CHANGE)
    // ------------------------------------------------------------------------

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
            System.err.println("⚠ FAILED to free slot.");
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
            System.err.println("⚠ FAILED to free slot.");
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
