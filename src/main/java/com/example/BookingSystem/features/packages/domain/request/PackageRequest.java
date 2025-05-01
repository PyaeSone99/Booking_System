package com.example.BookingSystem.features.packages.domain.request;

import com.example.BookingSystem.common.enums.Country;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;


public record PackageRequest(
        @NotBlank(message = "name is required.")
        String name,
        @NotNull(message = "credits is required")
        Long credits,
        @NotNull(message = "price is required")
        Double price,
        @NotNull(message = "need to add package valid minute")
        Integer validMinutes
) {
}
