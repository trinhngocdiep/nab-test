package com.nab.voucher.provision.api;

import com.nab.voucher.provision.model.Voucher;
import com.nab.voucher.provision.service.ProvisionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/provision")
public class ProvisionController {

    private final ProvisionService provisionService;

    public ProvisionController(ProvisionService provisionService) {
        this.provisionService = provisionService;
    }

    @PostMapping
    public Voucher generateVoucher(@Valid @RequestBody VoucherGenRequest request) {
        return provisionService.generateVoucher(request);
    }

}
