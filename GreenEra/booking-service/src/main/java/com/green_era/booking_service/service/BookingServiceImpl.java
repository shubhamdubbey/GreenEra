package com.green_era.booking_service.service;

import com.green_era.booking_service.dto.*;
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
import java.util.concurrent.CompletableFuture;
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

    private static final String GARDENER_SERVICE = "gardenerService";
    private static final String USER_SERVICE = "userService";

    // -------------------- CREATE BOOKING --------------------
    @Override
    @Transactional
    public BookingDto createBooking(BookingDto dto) {
        if (dto == null) throw new IllegalArgumentException("Booking request cannot be null.");
        if (dto.getBookingDate() == null) throw new IllegalArgumentException("Booking date is required.");
        if (dto.getStartTime() == null) throw new IllegalArgumentException("Start time is required.");
        if (dto.getBookingDate().isBefore(LocalDate.now())) throw new IllegalArgumentException("Cannot book a past date.");
        if (dto.getBookingType() == null) dto.setBookingType(BookingType.REGULAR);
        dto.setStartTime(normalizeToSlot(dto.getStartTime()));
        dto.setEndTime(dto.getStartTime().plusMinutes(SLOT_DURATION_MINUTES));

        // Fetch gardeners (Resilience-safe)
        List<GardenerDto> allGardeners = getAllGardenersSafe();
        if (allGardeners == null || allGardeners.isEmpty())
            throw new IllegalStateException("Gardener service unavailable or no gardeners found.");

        List<GardenerDto> eligiblePool = filterGardenersByType(allGardeners, dto.getBookingType());

        eligiblePool = eligiblePool.stream()
                .filter(g -> g.getAvailable()
                        && (g.getWorkStartTime() == null || g.getWorkStartTime().isBefore(dto.getStartTime()))
                        && (g.getWorkEndTime() == null || g.getWorkEndTime().isAfter(dto.getEndTime())))
                .filter(g -> !isSlotBlocked(g.getEmail(), dto))
                .collect(Collectors.toList());

        if (eligiblePool.isEmpty() && dto.getBookingType() == BookingType.URGENT) {
            eligiblePool = filterGardenersByType(allGardeners, BookingType.REGULAR).stream()
                    .filter(g -> g.getAvailable() && !isSlotBlocked(g.getEmail(), dto))
                    .collect(Collectors.toList());
        }

        if (eligiblePool.isEmpty())
            throw new IllegalStateException("No gardeners available for this slot.");

        GardenerDto selected = eligiblePool.stream()
                .min(Comparator.comparingInt(Utility::getBookingCountForGardener))
                .orElseThrow(() -> new IllegalStateException("Could not select gardener."));

        // Fetch user (Resilience-safe)
        UserDto user = getUserSafe(dto.getUserEmail());
        if (user == null) throw new EntityNotFoundException("User not found: " + dto.getUserEmail());

        double price = calculatePrice(dto, selected.getHourlyRate());

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

        updateAvailabilitySafe(selected.getEmail(), false);
        GardenerAvaibilityDto slotDto = new GardenerAvaibilityDto(
                selected.getEmail(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime()
        );
        blockSlotSafe(slotDto);

        return Mapper.bookingToBookingDto(saved);
    }

    // -------------------- RESILIENCE4J FEIGN CALLS --------------------

    @CircuitBreaker(name = GARDENER_SERVICE, fallbackMethod = "getAllGardenersFallback")
    @Retry(name = GARDENER_SERVICE)
    @TimeLimiter(name = GARDENER_SERVICE)
    public List<GardenerDto> getAllGardenersSafe() {
        CompletableFuture<List<GardenerDto>> future = CompletableFuture.supplyAsync(() -> gardenerClient.getAllGardeners());
        return future.exceptionally(ex -> getAllGardenersFallback(ex)).join();
    }

    public List<GardenerDto> getAllGardenersFallback(Throwable e) {
        System.out.println("Gardener service DOWN → Empty list provided.");
        return Collections.emptyList();
    }

    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "getUserFallback")
    @Retry(name = USER_SERVICE)
    @TimeLimiter(name = USER_SERVICE)
    public UserDto getUserSafe(String email) {
        CompletableFuture<UserDto> future = CompletableFuture.supplyAsync(() -> userClient.getUser(email));
        return future.exceptionally(ex -> getUserFallback(email, ex)).join();
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

    @CircuitBreaker(name = GARDENER_SERVICE, fallbackMethod = "availabilityFallback")
    @Retry(name = GARDENER_SERVICE)
    @TimeLimiter(name = GARDENER_SERVICE)
    public void updateAvailabilitySafe(String email, boolean available) {
        CompletableFuture.runAsync(() -> gardenerClient.updateAvailability(email, available)).join();
    }

    public void availabilityFallback(String email, boolean available, Throwable e) {
        System.out.println("⚠ FAILED updating availability for " + email);
    }

    @CircuitBreaker(name = GARDENER_SERVICE, fallbackMethod = "blockSlotFallback")
    @Retry(name = GARDENER_SERVICE)
    @TimeLimiter(name = GARDENER_SERVICE)
    public void blockSlotSafe(GardenerAvaibilityDto dto) {
        CompletableFuture.runAsync(() -> gardenerClient.blockSlot(dto)).join();
    }

    public void blockSlotFallback(GardenerAvaibilityDto dto, Throwable e) {
        System.out.println("⚠ FAILED to block slot (fallback executed).");
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
