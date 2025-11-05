package com.green_era.booking_service.repository;

import com.green_era.booking_service.entity.BookingEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findBookingByUserEmail(String userEmail);

    List<BookingEntity> findBookingByGardenerEmail(String gardenerEmail);

    @Query("select count(b)>0 from BookingEntity b " +
            "where b.gardenerEmail = :gardenerEmail and b.bookingDate = :date " +
            "and ((b.startTime < :endTime) and (b.endTime > :startTime))")
    boolean existsOverlapping(@Param("gardenerEmail") String gardenerEmail,
                              @Param("date") LocalDate date,
                              @Param("startTime") LocalTime startTime,
                              @Param("endTime") LocalTime endTime);

    int countByGardenerEmailAndBookingDate(String gardenerEmail, LocalDate date);

    List<BookingEntity> findByGardenerEmailAndBookingDate(String gardenerEmail, LocalDate bookingDate);

    List<BookingEntity> findByBookingDateAndStartTime(LocalDate date, LocalTime startTime);

}
