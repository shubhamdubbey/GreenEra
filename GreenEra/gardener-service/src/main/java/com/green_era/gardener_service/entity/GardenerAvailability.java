package com.green_era.gardener_service.entity;

import jakarta.persistence.*;
import jdk.vm.ci.meta.Local;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Gardener_Availability")
public class GardenerAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "gardener_email")
    private String gardenerEmail;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "is_booked")
    private boolean isBooked;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGardenerEmail() {
        return gardenerEmail;
    }

    public void setGardenerEmail(String gardenerEmail) {
        this.gardenerEmail = gardenerEmail;
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

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}
