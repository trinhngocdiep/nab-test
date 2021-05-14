package com.nab.voucher.order.repository;

import com.nab.voucher.order.entity.VoucherOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoucherOrderRepository extends JpaRepository<VoucherOrder, Integer> {
    List<VoucherOrder> findByPhoneNumber(String phoneNumber);
}
