package com.example.BookingSystem.job;

import com.example.BookingSystem.features.packages.domain.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.time.LocalDateTime;

@Component
public class UserPackageExpiryJob implements Job {
    @Autowired
    private PackageService packageService;

    @Override
    public void execute(JobExecutionContext context) {
        System.out.println("UserPackageExpiryJob running at " + LocalDateTime.now());
        packageService.updateExpiredUserPackages();
    }
}