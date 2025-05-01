package com.example.BookingSystem.features.classes.domain.repository;

import com.example.BookingSystem.features.classes.domain.entity.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Classes,Long> , JpaSpecificationExecutor<Classes> {
}
