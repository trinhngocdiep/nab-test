package com.nab.voucher.provision;

import com.nab.voucher.provision.api.ProvisionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProvisionServiceApplicationTests {

    @Autowired
    private ProvisionController provisionController;

    @Test
    void contextLoads() {
        assertThat(provisionController).isNotNull();
    }

}
