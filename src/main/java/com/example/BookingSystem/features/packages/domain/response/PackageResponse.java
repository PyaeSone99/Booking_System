package com.example.BookingSystem.features.packages.domain.response;

import com.example.BookingSystem.common.enums.Country;
import com.example.BookingSystem.features.packages.domain.entity.Packages;

public record PackageResponse(
    Long id,
    String name,
    Country country,
    Integer expireMin,
    Long credits,
    Double price
) {
    public static PackageResponse from(Packages packages){
        return new PackageResponse(
                packages.getId(),
                packages.getName(),
                packages.getCountry(),
                packages.getValidMinutes(),
                packages.getCredits(),
                packages.getPrice()
        );
    }

}
