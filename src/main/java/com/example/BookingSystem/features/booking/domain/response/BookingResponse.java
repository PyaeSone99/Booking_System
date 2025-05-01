package com.example.BookingSystem.features.booking.domain.response;

import com.example.BookingSystem.common.enums.BookingStatus;
import com.example.BookingSystem.features.booking.domain.entity.Booking;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        String className,
        BookingStatus status,
        LocalDateTime bookingTime
) {

    public static BookingResponse from(Booking booking){
        return new BookingResponse(
                booking.getId(),
                booking.getClasses().getClassName(),
                booking.getStatus(),
                booking.getBookingTime()
        );
    }
}
