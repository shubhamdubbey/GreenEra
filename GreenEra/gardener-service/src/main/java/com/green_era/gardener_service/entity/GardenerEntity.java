package com.green_era.gardener_service.entity;

import com.green_era.gardener_service.utils.GardenerType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(
        name = "gardeners",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_gardener_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_gardener_locality", columnList = "locality"),
                @Index(name = "idx_gardener_type_available", columnList = "gardener_type, is_available")
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GardenerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // ---------------- BASIC INFO ----------------
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "locality")
    private String locality;

    // ---------------- AVAILABILITY ----------------
    @Column(name = "is_available")
    @Builder.Default
    private boolean isAvailable = true;

    @Column(name = "work_start_time")
    @Builder.Default
    private LocalTime workStartTime = LocalTime.of(8, 0); // default 8 AM

    @Column(name = "work_end_time")
    @Builder.Default
    private LocalTime workEndTime = LocalTime.of(18, 0); // default 6 PM

    // ---------------- TYPE & PERFORMANCE ----------------
    @Enumerated(EnumType.STRING)
    @Column(name = "gardener_type", nullable = false)
    @Builder.Default
    private GardenerType gardenerType = GardenerType.REGULAR;

    @Column(name = "rating")
    @Builder.Default
    private double rating = 0.0;

    @Column(name = "total_jobs_completed")
    @Builder.Default
    private int totalJobsCompleted = 0;

    @Column(name = "hourly_rate")
    @Builder.Default
    private double hourlyRate = 100.0; // default rate

    // ---------------- UTILITY METHODS ----------------
    public void incrementJobsCompleted() {
        this.totalJobsCompleted += 1;
    }

    public boolean isWithinWorkingHours(LocalTime time) {
        return !time.isBefore(workStartTime) && !time.isAfter(workEndTime);
    }

    public boolean canTakeUrgentBooking() {
        return gardenerType == GardenerType.URGENT || gardenerType == GardenerType.BOTH;
    }

    public boolean canTakeRegularBooking() {
        return gardenerType == GardenerType.REGULAR || gardenerType == GardenerType.BOTH;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LocalTime getWorkStartTime() {
        return workStartTime;
    }

    public void setWorkStartTime(LocalTime workStartTime) {
        this.workStartTime = workStartTime;
    }

    public LocalTime getWorkEndTime() {
        return workEndTime;
    }

    public void setWorkEndTime(LocalTime workEndTime) {
        this.workEndTime = workEndTime;
    }

    public GardenerType getGardenerType() {
        return gardenerType;
    }

    public void setGardenerType(GardenerType gardenerType) {
        this.gardenerType = gardenerType;
    }

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

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
