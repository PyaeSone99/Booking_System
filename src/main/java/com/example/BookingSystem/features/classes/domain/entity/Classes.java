package com.example.BookingSystem.features.classes.domain.entity;

import com.example.BookingSystem.common.enums.Country;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "classes")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Classes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;

    @Enumerated(EnumType.STRING)
    private Country country;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer requiredCredits;
    private Integer maxSlots;
}
