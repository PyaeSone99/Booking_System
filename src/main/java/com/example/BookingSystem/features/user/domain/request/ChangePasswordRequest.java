package com.example.BookingSystem.features.user.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotNull(message = "User id required")
        Long id,
        @Size(max = 255)
        @NotBlank(message = "oldpassword is required.")
        String oldPassword,
        @Size(max = 255)
        @NotBlank(message = "newpassword is required.")
        String newPassword
) {
}
