package com.green_era.booking_service.dto;

import com.green_era.booking_service.utils.BookingStatus;
import com.green_era.booking_service.utils.BookingType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingDto {

    private Long id;

    @NotBlank(message = "user mail id is must.")
    private String userEmail;

    private String gardenerEmail;

    @NotBlank(message = "bookingDate id is must.")
    private LocalDate bookingDate;

    @NotBlank(message = "startTime id is must.")
    private LocalTime startTime;

    @NotBlank(message = "endTime id is must.")
    private LocalTime endTime;

    @NotBlank(message = "bookingType id is must.")
    private BookingType bookingType;

    private BookingStatus bookingStatus;
}
