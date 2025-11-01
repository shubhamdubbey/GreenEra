package com.green_era.booking_service.dto;

import com.green_era.booking_service.utils.BookingStatus;
import com.green_era.booking_service.utils.BookingType;
import jakarta.persistence.Column;
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

    @NotBlank(message = "description is mandatory")
    private String serviceDescription;

    private double price;

    private String userName;

    private String userPhone;

    private String gardenerName;

    private String gardenerPhone;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
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

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
