package com.green_era.gardener_service.service;

import com.green_era.gardener_service.dto.BookingDto;
import com.green_era.gardener_service.dto.GardenerDto;
import com.green_era.gardener_service.utils.AccountNotFoundException;
import com.green_era.gardener_service.utils.DuplicateAccountException;
import com.green_era.gardener_service.utils.GardenerType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GardenerService {
    GardenerDto registerGardener(GardenerDto gardenerDto) throws DuplicateAccountException;
    List<GardenerDto> getAllGardeners();
    GardenerDto getGardenerById(Long id) throws AccountNotFoundException;
    GardenerDto getGardenerByEmail(String email) throws AccountNotFoundException;
    String deleteGardener(Long id) throws AccountNotFoundException;
    List<GardenerDto> findByLocalityAndIsAvailableAndGardenerType(String locality, boolean availability, GardenerType gardenerType);
    GardenerDto updateAvailability(String email, Boolean available) throws AccountNotFoundException;
    String updateGardener(Long id, GardenerDto dto) throws AccountNotFoundException;
    GardenerDto markUnavailableByEmail(String email) throws AccountNotFoundException;
    List<BookingDto> getAllBookings(String email);
}
