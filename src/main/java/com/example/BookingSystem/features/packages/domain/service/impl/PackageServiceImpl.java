package com.example.BookingSystem.features.packages.domain.service.impl;

import com.example.BookingSystem.common.dto.PaginationDTO;
import com.example.BookingSystem.common.enums.Country;
import com.example.BookingSystem.features.packages.domain.entity.Packages;
import com.example.BookingSystem.features.packages.domain.entity.PurchasePackage;
import com.example.BookingSystem.features.packages.domain.repository.PackageRepository;
import com.example.BookingSystem.features.packages.domain.repository.PurchasePackageRepository;
import com.example.BookingSystem.features.packages.domain.request.PackageRequest;
import com.example.BookingSystem.features.packages.domain.request.PackageSearch;
import com.example.BookingSystem.features.packages.domain.response.PackageResponse;
import com.example.BookingSystem.features.packages.domain.response.PurchasePackageResponse;
//import com.example.BookingSystem.features.packages.domain.response.UserPurchaseResponse;
import com.example.BookingSystem.features.packages.domain.service.PackageService;
import com.example.BookingSystem.features.user.domain.entity.User;
import com.example.BookingSystem.features.user.domain.repository.UserRepository;
import com.example.BookingSystem.features.user.domain.response.LoginUserInfo;
import com.example.BookingSystem.features.user.domain.service.UserServices;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@Service
public class PackageServiceImpl implements PackageService {

    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UserServices userServices;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PurchasePackageRepository purchasePackageRepository;


    @Override
    public PackageResponse createPackage(PackageRequest packageRequest, Country country) {
        Packages packages = mapper.map(packageRequest,Packages.class);
        packages.setCountry(country);
        packageRepository.save(packages);
        return PackageResponse.from(packages);
    }

    @Override
    public PaginationDTO<PackageResponse> packageList(PackageSearch packageSearch, Pageable pageable) {
        Page<Packages> list = packageRepository.findAll(PackageSearch.search(packageSearch),pageable);
        List<PackageResponse> packageList = list.stream().map(PackageResponse::from).toList();
        return new PaginationDTO<>(packageList,list.getNumber(), list.getSize(),
                list.getPageable().getOffset(), list.getTotalElements(), list.getTotalPages());
    }

    @Override
    @Transactional
    public PurchasePackageResponse purchasePackage(Long packageId) {
        LoginUserInfo loginUserInfo = userServices.getLoginUserInfo();
        User user = userRepository.findById(loginUserInfo.id()).orElseThrow(() -> new NoSuchElementException("Login User Not Found"));
        Packages pkg = packageRepository.findById(packageId).orElseThrow(() -> new NoSuchElementException("Package Not Found"));
        PurchasePackage purchasePackage = new PurchasePackage();
        purchasePackage.setUser(user);
        purchasePackage.setPkg(pkg);
        purchasePackage.setRemainingCredits(pkg.getCredits());
        purchasePackage.setExpiryDateTime(LocalDateTime.now().plusMinutes(pkg.getValidMinutes()));
        purchasePackage.setExpired(false);
        purchasePackageRepository.save(purchasePackage);
        return PurchasePackageResponse.from(purchasePackage);
    }

    @Override
    public PaginationDTO<PurchasePackageResponse> purchasePackageList(Pageable pageable) {
        LoginUserInfo loginUserInfo = userServices.getLoginUserInfo();
        Page<PurchasePackage> purchasePackageList = purchasePackageRepository.findByUserId(loginUserInfo.id(),pageable);
        List<PurchasePackageResponse> list = purchasePackageList.stream().map(PurchasePackageResponse::from).toList();
//        Long totalCreditRemain = list.stream()
//                .map(PurchasePackageResponse::credits)
//                .filter(Objects::nonNull)
//                .reduce(0L, Long::sum);
        return new PaginationDTO<>(list,purchasePackageList.getNumber(), purchasePackageList.getSize(),
                purchasePackageList.getPageable().getOffset(), purchasePackageList.getTotalElements(), purchasePackageList.getTotalPages());
    }

    @Override
    @Transactional
    public void updateExpiredUserPackages() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Updating Purchase Package Status Schedular is run");
        List<PurchasePackage> expired = purchasePackageRepository.findByExpiredFalseAndExpiryDateTimeBefore(now);
        for (PurchasePackage up : expired) {
            up.setExpired(true);
            purchasePackageRepository.save(up);
        }
        log.info("Updating Purchase Package Status Schedular is run");
    }
}


























