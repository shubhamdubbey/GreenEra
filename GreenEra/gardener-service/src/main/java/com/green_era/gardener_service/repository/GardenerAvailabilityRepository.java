package com.green_era.gardener_service.repository;

import com.green_era.gardener_service.entity.GardenerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GardenerAvailabilityRepository extends JpaRepository<GardenerAvailability, Long> {
    List<GardenerAvailability> findByGardenerEmailAndDate(String email, LocalDate date);
    List<GardenerAvailability> findByGardenerEmail(String email);
    Optional<GardenerAvailability> findByGardenerEmailAndDateAndStartTime(String email, LocalDate date, LocalTime time);
}
