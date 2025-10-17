package com.green_era.user_service.dto;

import com.green_era.user_service.utils.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterUserDto {
    @NotBlank(message = "First name is mandatory")
    String firstName;

    @NotBlank(message = "Last name is mandatory")
    String lastName;

    @NotBlank(message = "Email is mandatory")
    String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must contain at least 1 uppercase, 1 lowercase, 1 number, and 1 special character")
    String password;

    @NotBlank(message = "Phone number is mandatory")
    String phoneNumber;

    @NotBlank(message = "Role is mandatory")
    RoleEnum role;
}
