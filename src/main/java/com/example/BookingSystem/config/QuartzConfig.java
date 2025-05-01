package com.example.BookingSystem.config;


import com.example.BookingSystem.job.UserPackageExpiryJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail userPackageExpiryJobDetail() {
        return JobBuilder.newJob(UserPackageExpiryJob.class)
                .withIdentity("userPackageExpiryJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger userPackageExpiryJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
//                .withIntervalInMinutes(1) // run every 1 min
                .withIntervalInHours(1) // run every 1 hour
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(userPackageExpiryJobDetail())
                .startNow()
                .withIdentity("userPackageExpiryTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
