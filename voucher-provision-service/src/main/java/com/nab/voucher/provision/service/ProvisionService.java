package com.nab.voucher.provision.service;

import com.nab.voucher.provision.api.VoucherGenRequest;
import com.nab.voucher.provision.model.Voucher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// TODO: Just a dummy implementation. It should call different 3rd party apis, depending on the `telco` parameter.
@Service
public class ProvisionService {

    private static final Logger log = LoggerFactory.getLogger(ProvisionService.class);

    @Value("${simulation.api.min-response-time-seconds}")
    int minResponseTime;
    @Value("${simulation.api.max-response-time-seconds}")
    int maxResponseTime;

    public Voucher generateVoucher(VoucherGenRequest request) {
        try {
            int simulationTime = ThreadLocalRandom.current().nextInt(minResponseTime, maxResponseTime);
            log.info("Generating voucher in {} seconds", simulationTime);
            Thread.sleep(simulationTime * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Generating voucher completed.");
        return new Voucher(request.getTelco(), request.getValue(), UUID.randomUUID().toString());
    }

    @PostConstruct
    public void init() {
        log.info("Simulating API response time: {} - {} seconds.", minResponseTime, maxResponseTime);
    }

}
