package com.green_era.gardener_service.repository;

import com.green_era.gardener_service.entity.GardenerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GardenerAvailabilityRepository extends JpaRepository<GardenerAvailability, Long> {
    List<GardenerAvailability> findByGardenerEmailAndDate(String email, LocalDate date);
}
