package com.green_era.gardener_service.repository;

import com.green_era.gardener_service.entity.GardenerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GardenerRepository extends JpaRepository<GardenerEntity, Long> {
    Optional<GardenerEntity> findByEmail(String email);
    Optional<GardenerEntity> findByPhoneNumber(String phoneNumber);
    List<GardenerEntity> findByLocalityAndAvailable(String locality, boolean available);
}
