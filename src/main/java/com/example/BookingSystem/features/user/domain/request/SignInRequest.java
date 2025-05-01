package com.example.BookingSystem.features.user.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInRequest(

        @Email
        @Size(max = 255)
        @NotBlank(message = "email is required.")
        String email,
        @Size(max = 255)
        @NotBlank(message = "password is required.")
        String password
) {
}
