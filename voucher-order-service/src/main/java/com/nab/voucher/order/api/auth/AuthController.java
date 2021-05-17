package com.nab.voucher.order.api.auth;

import com.nab.voucher.order.service.AuthService;
import com.nab.voucher.order.service.NotificationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final NotificationService notificationService;

    public AuthController(AuthenticationManager authenticationManager, AuthService authService, NotificationService notificationService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.notificationService = notificationService;
    }

    @PostMapping("/generate-otp")
    public GetOtpResponse getOtp(@Valid @RequestBody GetOtpRequest request) {
        String otp = authService.generateOtp(request.getPhoneNumber());
        notificationService.sendOtp(request.getPhoneNumber(), otp);
        return new GetOtpResponse("OTP sent to " + request.getPhoneNumber());
    }

    @PostMapping("/validate-otp")
    public ValidateOtpResponse validateOtp(@Valid @RequestBody ValidateOtpRequest request) {
        Authentication token = new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getOtp());
        Authentication authentication = authenticationManager.authenticate(token);
        User user = (User) authentication.getPrincipal();
        String authToken = authService.generateAuthToken(user);
        return new ValidateOtpResponse(authToken);
    }

}
