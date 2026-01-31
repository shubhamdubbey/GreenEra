package com.green_era.gardener_service.service;

import com.green_era.gardener_service.dto.BookingDto;
import com.green_era.gardener_service.dto.GardenerAvaibilityDto;
import com.green_era.gardener_service.dto.GardenerDto;
import com.green_era.gardener_service.entity.GardenerAvailability;
import com.green_era.gardener_service.entity.GardenerEntity;
import com.green_era.gardener_service.feign.BookingClient;
import com.green_era.gardener_service.repository.GardenerAvailabilityRepository;
import com.green_era.gardener_service.repository.GardenerRepository;
import com.green_era.gardener_service.utils.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class GardenerServiceImpl implements GardenerService {

    @Autowired
    BookingClient bookingClient;

    @Autowired
    GardenerRepository gardenerRepository;

    @Autowired
    GardenerAvailabilityRepository gardenerAvailabilityRepository;

    private static final Logger log = LoggerFactory.getLogger(GardenerServiceImpl.class);

    private static final String BOOKING_SERVICE = "bookingService";

    // -------------------- LOCAL DB OPS (No Resilience needed) --------------------

    @Override
    public GardenerDto registerGardener(GardenerDto gardenerDto) throws DuplicateAccountException {
        Optional<GardenerEntity> existingEmail = gardenerRepository.findByEmail(gardenerDto.getEmail());
        if (existingEmail.isPresent())
            throw new DuplicateAccountException("Account already exists with given email id.");
        Optional<GardenerEntity> existingPhone = gardenerRepository.findByPhoneNumber(gardenerDto.getPhoneNumber());
        if (existingPhone.isPresent())
            throw new DuplicateAccountException("Account already exists with given phone number.");
        GardenerEntity gardener = Mapper.gardenerDtoToEntity(gardenerDto);
        gardener = gardenerRepository.save(gardener);
        return Mapper.gardenerEntityToDto(gardener);
    }

    @Override
    public List<GardenerDto> getAllGardeners() {
        List<GardenerDto> list = new ArrayList<>();
        gardenerRepository.findAll().forEach(g -> list.add(Mapper.gardenerEntityToDto(g)));
        return list;
    }

    @Override
    public GardenerDto getGardenerById(Long id) throws AccountNotFoundException {
        return gardenerRepository.findById(id)
                .map(Mapper::gardenerEntityToDto)
                .orElseThrow(() -> new AccountNotFoundException("No gardener found with the given id."));
    }

    @Override
    public GardenerDto getGardenerByEmail(String email) throws AccountNotFoundException {
        return gardenerRepository.findByEmail(email)
                .map(Mapper::gardenerEntityToDto)
                .orElseThrow(() -> new AccountNotFoundException("Gardener not registered with given email id."));
    }

    @Override
    public String deleteGardener(Long id) throws AccountNotFoundException {
        GardenerEntity entity = gardenerRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("No gardener found with the given id."));
        gardenerRepository.delete(entity);
        return "Success";
    }

    @Override
    public List<GardenerDto> findByLocalityAndIsAvailableAndGardenerType(String locality, boolean availability, GardenerType gardenerType) {
        List<GardenerDto> dtoList = new ArrayList<>();
        gardenerRepository.findByLocalityAndIsAvailableAndGardenerType(locality, availability, gardenerType)
                .forEach(g -> dtoList.add(Mapper.gardenerEntityToDto(g)));
        return dtoList;
    }

    @Override
    public GardenerDto updateAvailability(String email, Boolean available) throws AccountNotFoundException {
        GardenerEntity entity = gardenerRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("No gardener found with given email id."));
        entity.setAvailable(available);
        gardenerRepository.save(entity);
        return Mapper.gardenerEntityToDto(entity);
    }

    @Override
    public String updateGardener(Long id, GardenerDto dto) throws AccountNotFoundException {
        gardenerRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("No gardener found with the given id."));
        GardenerEntity entity = Mapper.gardenerDtoToEntity(dto);
        gardenerRepository.save(entity);
        return "Success";
    }

    @Override
    public GardenerDto markUnavailableByEmail(String email) throws AccountNotFoundException {
        GardenerEntity entity = gardenerRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("No gardener found with the given email id."));
        entity.setAvailable(false);
        gardenerRepository.save(entity);
        return Mapper.gardenerEntityToDto(entity);
    }

    @Override
    public String BlockGardenerSlot(GardenerAvaibilityDto dto) {
        GardenerAvailability availability = new GardenerAvailability();
        availability.setGardenerEmail(dto.getEmail());
        availability.setDate(dto.getDate());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        availability.setBooked(true);
        gardenerAvailabilityRepository.save(availability);
        return "success";
    }

    @Override
    public List<GardenerAvaibilityDto> getBlockedSlots(String email, LocalDate date) {
        List<GardenerAvaibilityDto> list = new ArrayList<>();
        gardenerAvailabilityRepository.findByGardenerEmailAndDate(email, date)
                .forEach(a -> {
                    GardenerAvaibilityDto dto = new GardenerAvaibilityDto();
                    dto.setEmail(a.getGardenerEmail());
                    dto.setDate(a.getDate());
                    dto.setStartTime(a.getStartTime());
                    dto.setEndTime(a.getEndTime());
                    list.add(dto);
                });
        return list;
    }

    @Override
    public String deleteBlockedSlots(String email, LocalDate date, LocalTime time) {
        gardenerAvailabilityRepository.findByGardenerEmailAndDateAndStartTime(email, date, time)
                .ifPresent(gardenerAvailabilityRepository::delete);
        return "success";
    }

    // -------------------- FEIGN CALLS WITH ASYNC RESILIENCE4J --------------------

    @Override
    @CircuitBreaker(name = BOOKING_SERVICE, fallbackMethod = "fallbackBookings")
    @Retry(name = BOOKING_SERVICE)
    @TimeLimiter(name = BOOKING_SERVICE)
    public List<BookingDto> getAllBookings(String email) {
        CompletableFuture<List<BookingDto>> future = CompletableFuture.supplyAsync(() -> bookingClient.getBookingsbyGardener(email));
        return future.exceptionally(ex -> fallbackBookings(email, ex)).join();
    }

    public List<BookingDto> fallbackBookings(String email, Throwable ex) {
        log.error("Booking service unavailable for email {} → Fallback activated. Reason: {}", email, ex.getMessage());
        return new ArrayList<>();
    }
}
