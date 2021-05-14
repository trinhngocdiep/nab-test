package com.nab.voucher.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void sendVoucherCode(String phoneNumber, String code) {
        log.info("Sending voucher code {} to {}", phoneNumber, code);
    }

    public void sendOtp(String phoneNumber, String otp) {
        log.info("Sending OTP to {}", phoneNumber);
    }
}
