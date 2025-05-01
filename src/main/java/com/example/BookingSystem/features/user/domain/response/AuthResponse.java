package com.example.BookingSystem.features.user.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("access_token")
        String accessToken,
        String email,
        Long id,
        String name
) {

}
