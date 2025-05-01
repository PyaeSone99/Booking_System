package com.example.BookingSystem.features.user.domain.service;

import com.example.BookingSystem.features.user.domain.request.ChangePasswordRequest;
import com.example.BookingSystem.features.user.domain.request.RegisterRequest;
import com.example.BookingSystem.features.user.domain.request.SignInRequest;
import com.example.BookingSystem.features.user.domain.response.AuthResponse;
import com.example.BookingSystem.features.user.domain.response.LoginUserInfo;
import com.example.BookingSystem.features.user.domain.response.UserResponse;

public interface UserServices {


    UserResponse register(RegisterRequest registerRequest);

    void verifyEmail(String email);

    AuthResponse login(SignInRequest signInRequest);

    UserResponse getProfile(Long id);

    void changePassword(ChangePasswordRequest changePasswordRequest);

    void requestPasswordReset(String email);

    void setNewPassword(String newPassword,String token);

    LoginUserInfo getLoginUserInfo();
}
