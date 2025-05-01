package com.example.BookingSystem.features.classes.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ClassRequest(
        @NotBlank(message = "Class Name cannot be blank")
        String className,
        @NotNull(message = "Class Start Time cannot be null")
        LocalDateTime startTime,
        @NotNull(message = "Class end time cannot be null")
        LocalDateTime endTime,
        @NotNull(message = "Required Credits cannot be null")
        Integer requiredCredits,
        @NotNull(message = "Max Slots cannot be null")
        Integer maxSlots
) {
}
