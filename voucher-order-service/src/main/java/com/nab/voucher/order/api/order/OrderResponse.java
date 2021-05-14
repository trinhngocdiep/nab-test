package com.nab.voucher.order.api.order;

public class OrderResponse {
    private final String voucherCode;
    private final String message;

    public String getVoucherCode() {
        return voucherCode;
    }

    public String getMessage() {
        return message;
    }

    public OrderResponse(String voucherCode, String message) {
        this.voucherCode = voucherCode;
        this.message = message;
    }

    public static OrderResponse completed(String code) {
        return new OrderResponse(code, null);
    }

    public static OrderResponse processing(String message) {
        return new OrderResponse(null, message);
    }
}
