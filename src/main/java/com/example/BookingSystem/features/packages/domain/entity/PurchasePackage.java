package com.example.BookingSystem.features.packages.domain.entity;

import com.example.BookingSystem.features.user.domain.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "purchase_packages")
public class PurchasePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Packages pkg;
    private Long remainingCredits;
    private LocalDateTime expiryDateTime;
    private boolean expired;
}
