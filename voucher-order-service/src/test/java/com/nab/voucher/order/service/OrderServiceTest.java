package com.nab.voucher.order.service;

import com.nab.voucher.order.api.order.OrderResponse;
import com.nab.voucher.order.entity.VoucherOrder;
import com.nab.voucher.order.repository.VoucherOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private OrderService test;

    @Mock
    ProvisionServiceClient provisionClient;

    @Mock
    VoucherOrderRepository orderRepository;

    @Mock
    NotificationService notificationService;

    @BeforeEach
    public void init() {
        test = new OrderService(orderRepository, provisionClient, notificationService);
    }

    @Test
    public void testProcessOrder_withInTime() {
        VoucherOrder order = new VoucherOrder();
        order.setVoucherTelco("test");
        order.setVoucherValue(1000);
        order.setPhoneNumber("123");
        doAnswer(new AnswersWithDelay(500, new Returns("test code")))
                .when(provisionClient).getVoucher("test", 1000);
        test.timeoutSeconds = 1;
        OrderResponse actualResponse = test.processOrder(order);
        assertThat(actualResponse.getVoucherCode()).isEqualTo("test code");
        verify(orderRepository).save(order);
    }

    @Test
    public void testProcessOrder_withTimeOut() {
        VoucherOrder order = new VoucherOrder();
        order.setVoucherTelco("test");
        order.setVoucherValue(1000);
        order.setPhoneNumber("123");
        doAnswer(new AnswersWithDelay(1500, new Returns("test code")))
                .when(provisionClient).getVoucher("test", 1000);
        test.timeoutSeconds = 1;
        OrderResponse actualResponse = test.processOrder(order);
        assertThat(actualResponse.getVoucherCode()).isNull();
        assertThat(actualResponse.getMessage()).isNotNull();
        verifyNoInteractions(orderRepository);
        verify(notificationService, timeout(2000)).sendVoucherCode("123", "test code");
        verify(orderRepository, timeout(2000)).save(order);
    }

}