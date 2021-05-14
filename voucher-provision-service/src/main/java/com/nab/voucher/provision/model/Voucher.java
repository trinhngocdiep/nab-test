package com.nab.voucher.provision.model;

public class Voucher {
    private final String telco;
    private final int value;
    private final String code;

    public Voucher(String telco, int value, String code) {
        this.telco = telco;
        this.value = value;
        this.code = code;
    }

    public String getTelco() {
        return telco;
    }

    public int getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }
}

