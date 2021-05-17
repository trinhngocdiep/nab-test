package com.nab.voucher.provision.service;

import com.nab.voucher.provision.api.VoucherGenRequest;
import com.nab.voucher.provision.model.Voucher;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProvisionServiceTest {

    private final ProvisionService test = new ProvisionService();

    @Test
    public void generateVoucher() {
        test.minResponseTime = 0;
        test.maxResponseTime = 1;
        VoucherGenRequest request = new VoucherGenRequest();
        Voucher voucher = test.generateVoucher(request);
        assertThat(voucher.getCode()).isNotNull();
    }
}