package com.green_era.gardener_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gardeners")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GardenerEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "locality")
    private String locality;

    @Column(name = "is_available")
    @Builder.Default
    private boolean isAvailable = true;

    @Column(name = "rating")
    @Builder.Default
    private double rating = 0.0;

    @Column(name = "total_jobs_completed")
    @Builder.Default
    private int totalJobsCompleted = 0;

    @Column(name = "is_urgent_type")
    private boolean isUrgentType;

    @Column(name = "hourly_rate")
    private double hourlyRate;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotalJobsCompleted() {
        return totalJobsCompleted;
    }

    public void setTotalJobsCompleted(int totalJobsCompleted) {
        this.totalJobsCompleted = totalJobsCompleted;
    }

    public double getHourlyRate(){
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate){
        this.hourlyRate = hourlyRate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isUrgentType() {
        return isUrgentType;
    }

    public void setUrgentType(boolean urgentType) {
        isUrgentType = urgentType;
    }
}
