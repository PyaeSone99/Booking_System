package com.example.BookingSystem.features.booking.domain.entity;


import com.example.BookingSystem.common.enums.BookingStatus;
import com.example.BookingSystem.features.classes.domain.entity.Classes;
import com.example.BookingSystem.features.packages.domain.entity.PurchasePackage;
import com.example.BookingSystem.features.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Classes classes;

    @ManyToOne
    private PurchasePackage purchasePackage;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime bookingTime;
}
