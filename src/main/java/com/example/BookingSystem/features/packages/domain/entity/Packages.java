package com.example.BookingSystem.features.packages.domain.entity;

import com.example.BookingSystem.common.enums.Country;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "packages")
public class Packages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long credits;
    private Double price;

    @Enumerated(EnumType.STRING)
    private Country country;
    private int validMinutes;
}