package com.nab.voucher.order.api;

import com.nab.voucher.order.api.auth.AuthController;
import com.nab.voucher.order.service.AuthService;
import com.nab.voucher.order.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    public void generateOtp_sendSms() throws Exception {
        when(authService.generateOtp("123")).thenReturn("1234");
        mockMvc.perform(post("/v1/auth/generate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNumber\":\"123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsStringIgnoringCase("OTP sent")));
        verify(notificationService).sendOtp("123", "1234");
    }

    @Test
    public void validateOtp_return401_incorrectOtp() throws Exception {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad otp"));
        String requestJson = "{" +
                "\"phoneNumber\":\"123\"}," +
                "\"otp\":\"123\"}" +
                "";
        mockMvc.perform(post("/v1/auth/validate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(authService);
    }

    @Test
    public void validateOtp_return200_correctOtp() throws Exception {
        User user = mock(User.class);
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null));
        when(authService.generateAuthToken(any(User.class))).thenReturn("test jwt token");
        String requestJson = "{" +
                "\"phoneNumber\":\"123\"}," +
                "\"otp\":\"123\"}" +
                "";
        mockMvc.perform(post("/v1/auth/validate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("test jwt token")));
    }

}

