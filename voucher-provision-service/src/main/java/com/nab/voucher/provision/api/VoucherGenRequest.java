package com.nab.voucher.provision.api;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class VoucherGenRequest {
    @NotBlank
    private String telco;

    @Min(0)
    @Max(500_000)
    private int value;

    public String getTelco() {
        return telco;
    }

    public void setTelco(String telco) {
        this.telco = telco;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
