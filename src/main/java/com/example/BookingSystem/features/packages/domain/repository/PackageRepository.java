package com.example.BookingSystem.features.packages.domain.repository;

import com.example.BookingSystem.features.packages.domain.entity.Packages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<Packages,Long> , JpaSpecificationExecutor<Packages> {
}
