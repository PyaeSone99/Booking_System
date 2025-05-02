package com.example.BookingSystem.features.booking.api;

import com.example.BookingSystem.annotation.APIVersion;
import com.example.BookingSystem.common.dto.ApiResponseDTO;
import com.example.BookingSystem.common.dto.PaginationDTO;
import com.example.BookingSystem.features.booking.domain.response.BookingResponse;
import com.example.BookingSystem.features.booking.domain.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@APIVersion
@Tag(name = "Booking Api" , description = "This is api for booking")
public class BookingController {

    @Autowired
    private BookService bookingService;

    @PostMapping("/book")
    @Operation(summary = "This is api for Booking class")
    public ResponseEntity<ApiResponseDTO<BookingResponse>> bookClass(
            @RequestParam Long classId
    ) {
        return ResponseEntity.ok(new ApiResponseDTO<>(bookingService.bookClass(classId)));
    }

    @PostMapping("/book/cancel")
    @Operation(summary = "This is api for Booking Cancelling")
    public ResponseEntity<ApiResponseDTO<String>> cancelBooking(
            @RequestParam Long bookingId
    ) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(new ApiResponseDTO<>("Booking Cancel Successfully"));
    }

    @PostMapping("/book/check-in")
    @Operation(summary = "This is api for Booking Check in")
    public ResponseEntity<ApiResponseDTO<String>> checkIn(
            @RequestParam Long bookingId
    ) {
        bookingService.checkIn(bookingId);
        return ResponseEntity.ok(new ApiResponseDTO<>("Booking Check In Successfully"));
    }

    @GetMapping("/book/user-booking-list")
    @Operation(summary = "This is api for User booking list")
    public ResponseEntity<ApiResponseDTO<PaginationDTO<BookingResponse>>> userBookingList(
        Pageable pageable
    ){
        return ResponseEntity.ok(new ApiResponseDTO<>(bookingService.getUserBookingList(pageable)));
    }
}
