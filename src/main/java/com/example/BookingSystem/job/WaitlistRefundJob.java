package com.example.BookingSystem.job;

import com.example.BookingSystem.features.booking.domain.service.BookService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WaitlistRefundJob implements Job {

    @Autowired
    private BookService bookService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long classId = context.getJobDetail().getJobDataMap().getLong("classId");
        System.out.println("Waitlist refund job is run");
        bookService.refundWaitlistCredits(classId);
    }
}
