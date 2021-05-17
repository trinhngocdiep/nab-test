package com.nab.voucher.provision.api;

import com.nab.voucher.provision.model.Voucher;
import com.nab.voucher.provision.service.ProvisionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProvisionController.class)
class ProvisionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProvisionService provisionService;

    @Test
    public void generateVoucher_delegateToService() throws Exception {
        Voucher voucher = new Voucher("testTelco", 123, "456");
        when(provisionService.generateVoucher(any())).thenReturn(voucher);
        String requestBody = "{\"telco\":\"testTelco\", \"value\":10000}";
        mockMvc.perform(post("/v1/provision").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("456")))
                .andExpect(jsonPath("$.value", is(123)))
                .andExpect(jsonPath("$.telco", is("testTelco")));
    }
}