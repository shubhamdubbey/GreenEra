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
}
