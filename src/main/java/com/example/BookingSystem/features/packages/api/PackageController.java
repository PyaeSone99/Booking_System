package com.example.BookingSystem.features.packages.api;

import com.example.BookingSystem.annotation.APIVersion;
import com.example.BookingSystem.common.dto.ApiResponseDTO;
import com.example.BookingSystem.common.dto.PaginationDTO;
import com.example.BookingSystem.common.enums.Country;
import com.example.BookingSystem.features.packages.domain.request.PackageRequest;
import com.example.BookingSystem.features.packages.domain.request.PackageSearch;
import com.example.BookingSystem.features.packages.domain.response.PackageResponse;
import com.example.BookingSystem.features.packages.domain.response.PurchasePackageResponse;
import com.example.BookingSystem.features.packages.domain.service.PackageService;
import com.example.BookingSystem.features.user.domain.request.RegisterRequest;
import com.example.BookingSystem.features.user.domain.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@APIVersion
@Tag(name = "Package", description = "This is all Package Api")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @PostMapping("/packages")
    @Operation(summary = "Create Packages")
    public ResponseEntity<ApiResponseDTO<PackageResponse>> createPackage(
            @Valid @RequestBody PackageRequest packageRequest,
            @RequestParam Country country
            ){
        return ResponseEntity.ok(new ApiResponseDTO<>(packageService.createPackage(packageRequest,country)));
    }

    @GetMapping("/packages/list")
    @Operation(summary = "Getting Package List")
    public ResponseEntity<ApiResponseDTO<PaginationDTO<PackageResponse>>> packageList(
            PackageSearch packageSearch,
            Pageable pageable
    ){
        return ResponseEntity.ok(new ApiResponseDTO<>(packageService.packageList(packageSearch,pageable)));
    }

    @PostMapping("/packages/buy-package")
    @Operation(summary = "Buying Package")
    public ResponseEntity<ApiResponseDTO<PurchasePackageResponse>> purchasePackage(
            @RequestParam Long packageId
    ){
        return ResponseEntity.ok(new ApiResponseDTO<>(packageService.purchasePackage(packageId)));
    }


    @GetMapping("/puckages/user-packages")
    @Operation(summary = "User purchase packages list")
    public ResponseEntity<ApiResponseDTO<PaginationDTO<PurchasePackageResponse>>> purchasePackageList(
            Pageable pageable
    ){
        return ResponseEntity.ok(new ApiResponseDTO<>(packageService.purchasePackageList(pageable)));
    }
}

























