package com.example.BookingSystem.features.packages.domain.service;

import com.example.BookingSystem.common.dto.PaginationDTO;
import com.example.BookingSystem.common.enums.Country;
import com.example.BookingSystem.features.packages.domain.request.PackageRequest;
import com.example.BookingSystem.features.packages.domain.request.PackageSearch;
import com.example.BookingSystem.features.packages.domain.response.PackageResponse;
import com.example.BookingSystem.features.packages.domain.response.PurchasePackageResponse;
import org.springframework.data.domain.Pageable;

public interface PackageService {

    PackageResponse createPackage (PackageRequest packageRequest, Country country);

    PaginationDTO<PackageResponse> packageList (PackageSearch packageSearch, Pageable pageable);

    PurchasePackageResponse purchasePackage(Long packageId);

    PaginationDTO<PurchasePackageResponse> purchasePackageList (Pageable pageable);

    void updateExpiredUserPackages();
}
