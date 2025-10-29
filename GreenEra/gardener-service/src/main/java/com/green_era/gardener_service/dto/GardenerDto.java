package com.green_era.gardener_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GardenerDto {
    private Long id;

    @NotBlank(message = "email id is mandatory")
    private String email;   //maps to user's email

    @NotBlank(message = "name is mandatory")
    private String name;

    @NotBlank(message = "phone number is mandatory")
    private String phoneNumber;

    @NotBlank(message = "locality is mandatory")
    private String locality;

    @NotBlank(message = "available is mandatory")
    private Boolean available;

    @NotBlank(message =  "Hourly rate is mandatory")
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
