package com.example.BookingSystem.features.user.domain.service.impl;

import com.example.BookingSystem.exception.CoreApiException;
import com.example.BookingSystem.features.user.domain.entity.User;
import com.example.BookingSystem.features.user.domain.repository.UserRepository;
import com.example.BookingSystem.features.user.domain.request.ChangePasswordRequest;
import com.example.BookingSystem.features.user.domain.request.RegisterRequest;
import com.example.BookingSystem.features.user.domain.request.SignInRequest;
import com.example.BookingSystem.features.user.domain.response.AuthResponse;
import com.example.BookingSystem.features.user.domain.response.LoginUserInfo;
import com.example.BookingSystem.features.user.domain.response.UserResponse;
import com.example.BookingSystem.features.user.domain.service.MockEmailService;
import com.example.BookingSystem.features.user.domain.service.UserServices;
import com.example.BookingSystem.util.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserServicesImpl implements UserServices {

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockEmailService emailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public UserResponse register(RegisterRequest registerRequest) {
        User user = mapper.map(registerRequest,User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        String token = UUID.randomUUID().toString();
        emailService.sendVerificationEmail(user.getEmail(), token);
        return UserResponse.from(savedUser);
    }

    @Override
    public void verifyEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("Email is wrong! Please kindly check your email"));
        user.setEmailVerified(true);
        userRepository.save(user);
    }


    @Override
    public AuthResponse login(SignInRequest signInRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.email(),
                        signInRequest.password()
                )
        );
        return getAuthResponse(signInRequest.email()) ;
    }

    @Override
    public UserResponse getProfile(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User Not Found"));
        return UserResponse.from(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(changePasswordRequest.id()).orElseThrow( () -> new NoSuchElementException("User Not Found"));
        if (!passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            throw new CoreApiException("Old password incorrect");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
        userRepository.save(user);
    }

    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("Email Not Found"));
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);
        emailService.sendResetPasswordEmail(email, token);
    }

    @Override
    public void setNewPassword(String newPassword, String token) {
        User user = userRepository.findByResetToken(token).orElseThrow( () -> new NoSuchElementException("Incorrect Token"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

    @Override
    public LoginUserInfo getLoginUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            var user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new CoreApiException("Fail to retrieve login user info"));
            return new LoginUserInfo(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );
        }
        return null;
    }

    private AuthResponse getAuthResponse(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        var jwtToken = jwtTokenProvider.generateToken(user);

        return new AuthResponse(jwtToken,user.getEmail(),user.getId(),user.getName());
    }
}
