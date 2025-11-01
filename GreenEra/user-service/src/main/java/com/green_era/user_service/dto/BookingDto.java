package com.green_era.user_service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingDto {

    private Long id;

    private String userEmail;

    private String gardenerEmail;

    private LocalDate bookingDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String bookingType;

    private String bookingStatus;

    private String serviceDescription;

    private double price;

    private String gardenerName;

    private String gardenerPhone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getGardenerEmail() {
        return gardenerEmail;
    }

    public void setGardenerEmail(String gardenerEmail) {
        this.gardenerEmail = gardenerEmail;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
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

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getGardenerName() {
        return gardenerName;
    }

    public void setGardenerName(String gardenerName) {
        this.gardenerName = gardenerName;
    }

    public String getGardenerPhone() {
        return gardenerPhone;
    }

    public void setGardenerPhone(String gardenerPhone) {
        this.gardenerPhone = gardenerPhone;
    }
}
