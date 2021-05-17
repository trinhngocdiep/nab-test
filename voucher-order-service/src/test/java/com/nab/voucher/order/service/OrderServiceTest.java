package com.nab.voucher.order.service;

import com.nab.voucher.order.api.order.OrderRequest;
import com.nab.voucher.order.api.order.OrderResponse;
import com.nab.voucher.order.entity.VoucherOrder;
import com.nab.voucher.order.repository.VoucherOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    public void getOrderHistory_delegateToRepository() {
        test.getOrderHistory("1234");
        verify(orderRepository).findByPhoneNumber("1234");
    }

    @Test
    public void createOrder_setRequiredAttributes() {
        OrderRequest request = new OrderRequest();
        request.setValue(123);
        request.setTelco("1234");
        request.setPhoneNumber("12345");
        when(orderRepository.save(any(VoucherOrder.class))).thenReturn(mock(VoucherOrder.class));
        doAnswer(new AnswersWithDelay(100, new ThrowsException(new RuntimeException("Some exception"))))
                .when(provisionClient).getVoucher(null, 0);
        test.timeoutSeconds = 1;
        test.createOrder(request);
        ArgumentCaptor<VoucherOrder> argumentCaptor = ArgumentCaptor.forClass(VoucherOrder.class);
        verify(orderRepository).save(argumentCaptor.capture());
        VoucherOrder actualOrder = argumentCaptor.getValue();
        assertThat(actualOrder).isNotNull();
        assertThat(actualOrder.getStatus().toString()).isEqualTo("PROCESSING");
        assertThat(actualOrder.getVoucherTelco()).isEqualTo("1234");
        assertThat(actualOrder.getPhoneNumber()).isEqualTo("12345");
        assertThat(actualOrder.getVoucherValue()).isEqualTo(123);
    }

    @Test
    public void processOrder_withInTimeLimit() {
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
    public void processOrder_withTimeOut() {
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