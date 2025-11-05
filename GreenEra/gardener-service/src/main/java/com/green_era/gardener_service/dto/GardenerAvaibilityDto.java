package com.green_era.gardener_service.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalTime;

public class GardenerAvaibilityDto {

    @NotBlank(message = "Gardener's email is mandatory")
    private String email;

    @NotBlank(message = "Date is mandatory")
    private LocalDate date;

    @NotBlank(message = "Start time is mandatory")
    private LocalTime startTime;

    @NotBlank(message = "End time is mandatory")
    private LocalTime endTime;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
