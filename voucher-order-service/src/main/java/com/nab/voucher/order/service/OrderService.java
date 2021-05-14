package com.nab.voucher.order.service;

import com.nab.voucher.order.api.order.OrderRequest;
import com.nab.voucher.order.api.order.OrderResponse;
import com.nab.voucher.order.entity.VoucherOrder;
import com.nab.voucher.order.repository.VoucherOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.*;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Value("${api.voucher-provision.timeout-seconds}")
    int timeoutSeconds;

    private final VoucherOrderRepository orderRepository;
    private final NotificationService notificationService;
    private final ProvisionServiceClient provisionClient;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public OrderService(VoucherOrderRepository orderRepository, ProvisionServiceClient provisionClient, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.provisionClient = provisionClient;
    }

    public List<VoucherOrder> getOrderHistory(String phoneNumber) {
        return orderRepository.findByPhoneNumber(phoneNumber);
    }

    public OrderResponse createOrder(OrderRequest request) {
        VoucherOrder order = saveNewOrder(request);
        return processOrder(order);
    }

    private VoucherOrder saveNewOrder(OrderRequest request) {
        VoucherOrder newOrder = new VoucherOrder();
        newOrder.setVoucherTelco(request.getTelco());
        newOrder.setVoucherValue(request.getValue());
        newOrder.setPhoneNumber(request.getPhoneNumber());
        newOrder.setStatus(VoucherOrder.VoucherOrderStatus.PROCESSING);
        return orderRepository.save(newOrder);
    }

    OrderResponse processOrder(VoucherOrder order) {
        Future<String> futureCode = invokeVoucherProvision(order);
        try {
            String code = futureCode.get(timeoutSeconds, TimeUnit.SECONDS);
            saveCompletedOrder(order, code);
            return OrderResponse.completed(code);
        } catch (InterruptedException | ExecutionException e) {
            handleErrorProvision(order, e);
        } catch (TimeoutException timeoutException) {
            handleSlowProvision(order, futureCode);
        }
        return OrderResponse.processing(String.format("The voucher will be sent to your phone number (%s) via SMS",
                order.getPhoneNumber()));
    }

    private void saveCompletedOrder(VoucherOrder order, String code) {
        order.setVoucherCode(code);
        order.setStatus(VoucherOrder.VoucherOrderStatus.COMPLETED);
        orderRepository.save(order);
    }

    private Future<String> invokeVoucherProvision(VoucherOrder order) {
        return executorService.submit(() ->
                provisionClient.getVoucher(order.getVoucherTelco(), order.getVoucherValue()));
    }

    private void handleErrorProvision(VoucherOrder order, Exception e) {
        log.error("Error issuing voucher: {}", order, e);
        // TODO: retry
    }

    private void handleSlowProvision(VoucherOrder order, Future<String> futureCode) {
        executorService.submit(() -> {
            try {
                String code = futureCode.get();
                saveCompletedOrder(order, code);
                notificationService.sendVoucherCode(order.getPhoneNumber(), code);
            } catch (ExecutionException | InterruptedException e) {
                handleErrorProvision(order, e);
            }
        });
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }
}
