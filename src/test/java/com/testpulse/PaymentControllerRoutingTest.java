package com.testpulse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testpulse.controller.PaymentController;
import com.testpulse.dto.PaymentRecordRequest;
import com.testpulse.dto.PaymentRecordResponse;
import com.testpulse.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerRoutingTest {

    @Test
    void shouldAcceptRecordsSuccessRoute() throws Exception {
        PaymentService paymentService = mock(PaymentService.class);
        when(paymentService.recordPayment(any(PaymentRecordRequest.class)))
                .thenReturn(PaymentRecordResponse.builder()
                        .success(true)
                        .message("Payment recorded successfully")
                        .paymentStatus("SUCCESS")
                        .build());

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PaymentController(paymentService)).build();
        ObjectMapper objectMapper = new ObjectMapper();

        PaymentRecordRequest request = new PaymentRecordRequest();
        request.setUserId(2L);
        request.setPlanId("plan_annual");
        request.setDurationDays(210);
        request.setAmountInPaise(5900L);
        request.setPaymentStatus("SUCCESS");

        mockMvc.perform(post("/api/payment/records-success")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
