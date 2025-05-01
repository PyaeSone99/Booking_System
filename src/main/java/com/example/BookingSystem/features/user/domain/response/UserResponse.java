package com.example.BookingSystem.features.user.domain.response;

import com.example.BookingSystem.features.user.domain.entity.User;

public record UserResponse (
        Long id,
        String name,
        String email,
        String country
) {
    public static UserResponse from ( User user){
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCountry()
        );
    }
}
