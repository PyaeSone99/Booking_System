package com.example.BookingSystem.features.packages.domain.repository;

import com.example.BookingSystem.common.enums.Country;
import com.example.BookingSystem.features.packages.domain.entity.PurchasePackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchasePackageRepository extends JpaRepository<PurchasePackage,Long> , JpaSpecificationExecutor<PurchasePackage> {

    Page<PurchasePackage> findByUserId(Long userId, Pageable pageable);

    List<PurchasePackage> findByExpiredFalseAndExpiryDateTimeBefore(LocalDateTime dateTime);
    List<PurchasePackage> findByUserIdAndPkg_CountryAndExpiredFalseAndRemainingCreditsGreaterThan(
            Long userId, Country country, int minCredits
    );

}
