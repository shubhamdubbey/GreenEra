package com.green_era.gardener_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GardenerDto {
    Long id;
    @NotBlank(message = "email id is mandatory")
    String email;   //maps to user's email

    @NotBlank(message = "name is mandatory")
    String name;

    @NotBlank(message = "phone number is mandatory")
    String phoneNumber;

    @NotBlank(message = "locality is mandatory")
    String locality;

    @NotBlank(message = "available is mandatory")
    Boolean available;

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
