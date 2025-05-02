package com.example.BookingSystem.features.booking.domain.service;

import com.example.BookingSystem.common.dto.PaginationDTO;
import com.example.BookingSystem.features.booking.domain.response.BookingResponse;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookingResponse bookClass(Long classId);

    void cancelBooking( Long bookingId);

    void checkIn(Long bookingId);

    void refundWaitlistCredits(Long classId);

    PaginationDTO<BookingResponse> getUserBookingList(Pageable pageable);
}
