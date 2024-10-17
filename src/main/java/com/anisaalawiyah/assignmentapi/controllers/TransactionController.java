package com.anisaalawiyah.assignmentapi.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.anisaalawiyah.assignmentapi.dto.TopUpRequestDto;
import com.anisaalawiyah.assignmentapi.dto.PaymentRequestDto;
import com.anisaalawiyah.assignmentapi.services.TransactionServices;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@ApiOperation(value = "Modul transaction", position = 3)
@RequestMapping("/")
@Tag(name = "3.Modul transaction")
public class TransactionController {

    @Autowired
    private TransactionServices transactionServices;


   
    @GetMapping("/balance")
    public ResponseEntity<String> getBalance() {
        try {
            int balance = transactionServices.getBalance();
            return ResponseEntity.ok("Saldo Anda adalah: " + balance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Terjadi kesalahan: " + e.getMessage());
        }
    }
   @PostMapping("/topup")
public ResponseEntity<?> topUp(@RequestBody TopUpRequestDto request) {
    try {
        int amount = request.getTop_up_amount();
        if (amount <= 0) {
            return ResponseEntity.badRequest().body("Jumlah top up harus lebih besar dari 0");
        }
        String result = transactionServices.topUp(amount);
        return ResponseEntity.ok(Map.of("message", result));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body("Terjadi kesalahan: " + e.getMessage());
    }
}

        
@PostMapping("/transaction")
public ResponseEntity<?> payment(@RequestBody PaymentRequestDto paymentRequest) {
    try {
        String serviceCode = paymentRequest.getServiceCode();
        if (serviceCode == null || serviceCode.isEmpty()) {
            return ResponseEntity.badRequest().body("Kode layanan harus disediakan");
        }
        Map<String, Object> result = transactionServices.payment(paymentRequest);
        return ResponseEntity.ok(result);
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body("Terjadi kesalahan: " + e.getMessage());
    }
}

@GetMapping("/transaction/history")
public ResponseEntity<?> getTransactionHistory(@RequestParam(required = false) Integer limit,@RequestParam(required = false) Integer offset) {
    try {
        List<Map<String, Object>> history = transactionServices.getTransactionHistory(limit,offset);
        return ResponseEntity.ok(history);
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body("Terjadi kesalahan: " + e.getMessage());
    }
}
}
