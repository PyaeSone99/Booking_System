package com.example.BookingSystem.features.classes.domain.response;

import com.example.BookingSystem.common.enums.Country;
import com.example.BookingSystem.features.classes.domain.entity.Classes;

import java.time.LocalDateTime;

public record ClassesResponse(
        Long id,
        String className,
        Country country,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer requiredCredits,
        Integer maxSlots,
        Integer bookCount
) {
    public static ClassesResponse from(Classes classes){
        return  new ClassesResponse(
                classes.getId(),
                classes.getClassName(),
                classes.getCountry(),
                classes.getStartTime(),
                classes.getEndTime(),
                classes.getRequiredCredits(),
                classes.getMaxSlots(),
                0
        );
    }

    public static ClassesResponse from(Classes classes,Integer bookCount){
        return  new ClassesResponse(
                classes.getId(),
                classes.getClassName(),
                classes.getCountry(),
                classes.getStartTime(),
                classes.getEndTime(),
                classes.getRequiredCredits(),
                classes.getMaxSlots(),
                bookCount
        );
    }
}
