package com.nab.voucher.order.api.auth;

public class GetOtpResponse {

    private String message;

    public GetOtpResponse(String message) {
        this.message =  message;
    }

    public String getMessage() {
        return message;
    }
}
