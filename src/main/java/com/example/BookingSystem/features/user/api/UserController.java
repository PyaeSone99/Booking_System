package com.example.BookingSystem.features.user.api;

import com.example.BookingSystem.annotation.APIVersion;
import com.example.BookingSystem.common.dto.ApiResponseDTO;
import com.example.BookingSystem.features.user.domain.request.ChangePasswordRequest;
import com.example.BookingSystem.features.user.domain.request.RegisterRequest;
import com.example.BookingSystem.features.user.domain.request.SignInRequest;
import com.example.BookingSystem.features.user.domain.response.AuthResponse;
import com.example.BookingSystem.features.user.domain.response.UserResponse;
import com.example.BookingSystem.features.user.domain.service.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@APIVersion
@Tag(name = "User", description = "This is all User Api")
public class UserController {

    @Autowired
    private UserServices userServices;

    @PostMapping("/users")
    @Operation(summary = "Sign up User")
    public ResponseEntity<ApiResponseDTO<UserResponse>> signUpUser(
            @Valid @RequestBody RegisterRequest userRequest
    ){
        return ResponseEntity.ok(new ApiResponseDTO<>(userServices.register(userRequest)));
    }

    @GetMapping("/email/verify")
    @Operation(summary = "Email Validation Api ")
    public ResponseEntity<ApiResponseDTO<String>> verifyEmail(
            @RequestParam String email)
    {
        userServices.verifyEmail(email);
        return ResponseEntity.ok(new ApiResponseDTO<>("Email verified"));
    }

    @PostMapping("/user/login")
    @Operation(summary = "User login api")
    public ResponseEntity<ApiResponseDTO<AuthResponse>> userLogin(
            @Valid @RequestBody SignInRequest loginRequest
    ) {
        var res = userServices.login(loginRequest);
        return ResponseEntity.ok(new ApiResponseDTO<>(res));
    }

    @GetMapping("/user/get-profile")
    @Operation(summary = "Getting User Profile")
    public ResponseEntity<ApiResponseDTO<UserResponse>> getProfile(
            @RequestParam Long id
    ){
        return ResponseEntity.ok(new ApiResponseDTO<>(userServices.getProfile(id)));
    }

    @PostMapping("/user/change-password")
    @Operation(summary = "Changing Password api")
    public ResponseEntity<ApiResponseDTO<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        userServices.changePassword(changePasswordRequest);
        return ResponseEntity.ok(new ApiResponseDTO<>("Password changed"));
    }

    @PostMapping("/user/reset-password")
    @Operation(summary = "Password Reset Requesting api")
    public ResponseEntity<ApiResponseDTO<String>> resetPassword(
            @RequestParam String email
    ) {
        userServices.requestPasswordReset(email);
        return ResponseEntity.ok(new ApiResponseDTO<>("Reset password email sent.(Please Kindly check to Log for token)"));
    }

    @PostMapping("/user/set-new-password")
    @Operation(summary = "Setting New Password api")
    public ResponseEntity<ApiResponseDTO<String>> setNewPassword(
            @RequestParam String newPassword,
            @RequestParam String token
    ) {
        userServices.setNewPassword(newPassword,token);
        return ResponseEntity.ok(new ApiResponseDTO("Password reset successful"));
    }

}




























