package com.example.BookingSystem.features.packages.domain.response;

import com.example.BookingSystem.common.enums.Country;
import com.example.BookingSystem.features.packages.domain.entity.PurchasePackage;

import java.time.LocalDateTime;

public record PurchasePackageResponse(
        Long id,
        String userName,
        String packageName,
        Long credits,
        LocalDateTime expiryDateTime,
        boolean expired,
        Country country
) {
    public static PurchasePackageResponse from(PurchasePackage pukg){
        return new PurchasePackageResponse(
                pukg.getId(),
                pukg.getUser().getName(),
                pukg.getPkg().getName(),
                pukg.getRemainingCredits(),
                pukg.getExpiryDateTime(),
                pukg.isExpired(),
                pukg.getPkg().getCountry()
        );
    }
}
