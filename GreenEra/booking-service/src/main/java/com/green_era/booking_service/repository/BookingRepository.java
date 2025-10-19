package com.green_era.booking_service.repository;

import com.green_era.booking_service.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findBookingByUserEmail(String userEmail);
    List<BookingEntity> findBookingByGardenerEmail(String gardenerEmail);
}
