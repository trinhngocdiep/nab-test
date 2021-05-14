package com.nab.voucher.order.api.order;

import com.nab.voucher.order.entity.VoucherOrder;
import com.nab.voucher.order.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place-order")
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @RolesAllowed("USER")
    @GetMapping("/order-history")
    public List<VoucherOrder> getHistory(@AuthenticationPrincipal User user) {
        return orderService.getOrderHistory(user.getUsername());
    }

}
