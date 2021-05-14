package com.nab.voucher.order.api.auth;

public class ValidateOtpResponse {
    private String token;

    public ValidateOtpResponse(String authToken) {
        this.token = authToken;
    }

    public String getToken() {
        return token;
    }
}
