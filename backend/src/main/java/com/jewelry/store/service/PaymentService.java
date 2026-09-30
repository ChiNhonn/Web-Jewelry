package com.jewelry.store.service;

import com.jewelry.store.dto.response.PaymentInitiateResponse;
import com.jewelry.store.dto.response.VNPayReturnResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {

    PaymentInitiateResponse initiatePayment(Long userId, String orderCode, HttpServletRequest request);

    VNPayReturnResponse processVNPayReturn(Map<String, String> queryParams);

    Map<String, String> processVNPayIpn(Map<String, String> queryParams);

    PaymentInitiateResponse getPaymentDetails(Long userId, String orderCode);
}
