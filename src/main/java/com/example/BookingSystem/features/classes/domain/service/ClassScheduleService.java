package com.example.BookingSystem.features.classes.domain.service;

import com.example.BookingSystem.features.booking.domain.service.BookService;
import com.example.BookingSystem.job.WaitlistRefundJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class ClassScheduleService {
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private BookService bookService;

    public void scheduleWaitlistRefundJob(Long classId, LocalDateTime endTime) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(WaitlistRefundJob.class)
                    .withIdentity("waitlistRefundJob_" + classId, "waitlistRefunds")
                    .usingJobData("classId", classId.toString())
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("waitlistRefundTrigger_" + classId, "waitlistRefunds")
                    .startAt(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
