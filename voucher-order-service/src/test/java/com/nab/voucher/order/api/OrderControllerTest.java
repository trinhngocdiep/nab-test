package com.nab.voucher.order.api;

import com.nab.voucher.order.api.order.OrderController;
import com.nab.voucher.order.api.order.OrderRequest;
import com.nab.voucher.order.api.order.OrderResponse;
import com.nab.voucher.order.entity.VoucherOrder;
import com.nab.voucher.order.service.AuthService;
import com.nab.voucher.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private OrderService service;


    @Test
    public void placeOrder_returnVoucherCode() throws Exception {
        when(service.createOrder(any(OrderRequest.class)))
                .thenReturn(new OrderResponse("test-voucher-123", null));
        mockMvc.perform(post("/v1/place-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOrderRequest("123", "telco", 10000)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voucherCode", is("test-voucher-123")));
    }

    @Test
    public void placeOrder_returnMessage() throws Exception {
        when(service.createOrder(any(OrderRequest.class)))
                .thenReturn(new OrderResponse(null, "Your voucher will come via SMS."));
        mockMvc.perform(post("/v1/place-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOrderRequest("123", "telco", 10000)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Your voucher will come via SMS.")))
                .andExpect(jsonPath("$.voucherCode", nullValue()));
    }

    @Test
    public void placeOrder_badInput() throws Exception {
        mockMvc.perform(post("/v1/place-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOrderRequest("", "", 100)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void orderHistory_unauthorized() throws Exception {
        mockMvc.perform(get("/v1/order-history"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void orderHistory_returnList() throws Exception {
        when(authService.validateAuthToken("123")).thenReturn(
                new User("123", "123", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
        when(service.getOrderHistory("123"))
                .thenReturn(Arrays.asList(createOrderHistory("code1"), createOrderHistory("code2")));
        mockMvc.perform(get("/v1/order-history").header("Authorization", "Bearer 123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].voucherCode", is("code1")))
                .andExpect(jsonPath("$[1].voucherCode", is("code2")));
    }

    private VoucherOrder createOrderHistory(String code) {
        VoucherOrder order = new VoucherOrder();
        order.setVoucherCode(code);
        return order;
    }

    private String createOrderRequest(String phoneNumber, String telco, int value) {
        return "{" +
                "\"telco\":\"" + telco + "\"," +
                "\"phoneNumber\":\"" + phoneNumber + "\"," +
                "\"value\":" + value +
                "}";
    }

}

