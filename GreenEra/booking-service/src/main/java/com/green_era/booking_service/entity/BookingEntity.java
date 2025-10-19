package com.green_era.booking_service.entity;

import com.green_era.booking_service.utils.BookingStatus;
import com.green_era.booking_service.utils.BookingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "booking")
@Data
public class BookingEntity {

    @Id
    @Column(name = "id")
    Long id;

    @Column(name = "user_email")
    String userEmail;

    @Column(name = "gardener_email")
    String gardenerEmail;

    @Column(name = "booking_date")
    LocalDate bookingDate;

    @Column(name = "startTime")
    LocalTime startTime;

    @Column(name = "endTime")
    LocalTime endTime;

    @Column(name = "booking_type")
    BookingType bookingType;

    @Column(name = "booking_status")
    BookingStatus bookingStatus;
}
