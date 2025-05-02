package com.example.BookingSystem.features.booking.domain.repository;

import com.example.BookingSystem.common.enums.BookingStatus;
import com.example.BookingSystem.features.booking.domain.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByClasses_IdAndStatus(Long classId, BookingStatus status);
    List<Booking> findByClasses_IdAndUser_IdAndStatusIn(Long classId, Long userId, List<BookingStatus> statuses);
    Page<Booking> findAllByUserId(Long userId, Pageable pageable);
}
