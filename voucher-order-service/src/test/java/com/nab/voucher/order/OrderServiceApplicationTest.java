package com.nab.voucher.order;

import com.nab.voucher.order.api.auth.AuthController;
import com.nab.voucher.order.api.order.OrderController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderServiceApplicationTest {

    @Autowired
    AuthController authController;

    @Autowired
    OrderController orderController;

    @Test
    public void contextLoads() {
        assertThat(authController).isNotNull();
        assertThat(orderController).isNotNull();
    }
}