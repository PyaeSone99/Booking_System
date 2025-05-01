package com.example.BookingSystem.job;

import com.example.BookingSystem.features.booking.domain.service.BookService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WaitlistRefundJob implements Job {
    @Autowired
    private BookService bookingService;

    @Override
    public void execute(JobExecutionContext context) {
        Long classId = context.getJobDetail().getJobDataMap().getLong("classId");
        bookingService.refundWaitlistCredits(classId);
        System.out.println("WaitlistRefundJob executed for classId: " + classId);
    }
}
