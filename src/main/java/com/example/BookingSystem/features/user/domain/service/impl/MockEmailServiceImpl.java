package com.example.BookingSystem.features.user.domain.service.impl;

import com.example.BookingSystem.features.user.domain.service.MockEmailService;
import org.springframework.stereotype.Service;

@Service
public class MockEmailServiceImpl implements MockEmailService {

    @Override
    public void sendVerificationEmail(String email,String token) {
        System.out.println("Verification email sent to " + email + " with token: " + token);
    }

    @Override
    public void sendResetPasswordEmail(String email, String token) {
        System.out.println("Reset password email sent to " + email + " with token: " + token);
    }
}
