package com.example.BookingSystem.features.booking.domain.service;

import com.example.BookingSystem.features.booking.domain.response.BookingResponse;

public interface BookService {

    BookingResponse bookClass(Long classId);

    void cancelBooking( Long bookingId);

    void checkIn(Long bookingId);

    void refundWaitlistCredits(Long classId);
}
