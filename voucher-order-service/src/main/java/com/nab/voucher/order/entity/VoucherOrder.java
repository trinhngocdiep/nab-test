package com.nab.voucher.order.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class VoucherOrder {

    @Id
    @GeneratedValue
    private Long id;

    private String phoneNumber;
    private String voucherTelco;
    private int voucherValue;
    private String voucherCode;
    private Date createdDate = new Date();
    private VoucherOrderStatus status = VoucherOrderStatus.PROCESSING;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVoucherTelco() {
        return voucherTelco;
    }

    public void setVoucherTelco(String voucherTelco) {
        this.voucherTelco = voucherTelco;
    }

    public int getVoucherValue() {
        return voucherValue;
    }

    public void setVoucherValue(int voucherValue) {
        this.voucherValue = voucherValue;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public VoucherOrderStatus getStatus() {
        return status;
    }

    public void setStatus(VoucherOrderStatus status) {
        this.status = status;
    }

    public enum VoucherOrderStatus {
        PROCESSING, COMPLETED
    }
}
