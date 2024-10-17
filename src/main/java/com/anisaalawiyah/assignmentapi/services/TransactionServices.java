package com.anisaalawiyah.assignmentapi.services;
import java.util.List;
import java.util.Map;

import com.anisaalawiyah.assignmentapi.dto.PaymentRequestDto;

public interface TransactionServices {
    int getBalance();
    String topUp(int amount);
    // Map<String, Object> payment(String serviceCode);
     Map<String, Object> payment(PaymentRequestDto paymentRequest);
     List<Map<String, Object>> getTransactionHistory(Integer limit, Integer offset);
}

