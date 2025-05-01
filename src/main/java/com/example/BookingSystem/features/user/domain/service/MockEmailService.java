package com.example.BookingSystem.features.user.domain.service;

public interface MockEmailService {

    void sendVerificationEmail(String email,String token);

    void sendResetPasswordEmail(String email,String token);
}
