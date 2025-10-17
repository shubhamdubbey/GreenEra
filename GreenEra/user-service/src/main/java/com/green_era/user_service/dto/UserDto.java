package com.green_era.user_service.dto;

import com.green_era.user_service.utils.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank(message = "First name is mandatory")
    String firstName;

    @NotBlank(message = "Last name is mandatory")
    String lastName;

    @NotBlank(message = "Email is mandatory")
    String email;

    @NotBlank(message = "Phone number is mandatory")
    String phoneNumber;

    @NotBlank(message = "Role is mandatory")
    RoleEnum role;
}
