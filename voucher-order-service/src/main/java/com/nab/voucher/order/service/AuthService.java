package com.nab.voucher.order.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collections;

// TODO: This is just a dummy api, it doesn't perform any validation.
@Service
public class AuthService {

    private final NotificationService notificationService;

    public AuthService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void generateOtp(String phoneNumber) {
        notificationService.sendOtp(phoneNumber, phoneNumber);
    }

    public User findUser(String phoneNumber) {
        return new User(phoneNumber, phoneNumber, Collections.emptyList());
    }

    public String generateAuthToken(User user) {
        return user.getUsername();
    }

    public User validateAuthToken(String token) {
        if (token != null) {
            return new User(token, token, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        }
        return null;
    }

    public void logout() {
    }
}
