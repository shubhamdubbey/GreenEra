package com.green_era.booking_service.dto;

import lombok.Data;

@Data
public class GardenerDto {
    private Long id;

    private String email;   //maps to user's email

    private String name;

    private String phoneNumber;

    private String locality;

    private Boolean available;

    private double hourlyRate;

    private double rating;

    private int jobsCompleted;

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getJobsCompleted() {
        return jobsCompleted;
    }

    public void setJobsCompleted(int jobsCompleted) {
        this.jobsCompleted = jobsCompleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
