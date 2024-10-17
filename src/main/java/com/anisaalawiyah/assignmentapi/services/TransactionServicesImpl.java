package com.anisaalawiyah.assignmentapi.services;

import org.springframework.transaction.annotation.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import java.util.List;
import java.util.Map;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.anisaalawiyah.assignmentapi.dto.PaymentRequestDto; // Tambahkan impor ini

@Service
public class TransactionServicesImpl implements TransactionServices {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TransactionServicesImpl.class);

  

    @Override
    public int getBalance() {
        String email = getCurrentUserEmail();
        String sql = "SELECT balance FROM registration WHERE email = ?";
        try {
            Integer balance = jdbcTemplate.queryForObject(sql, Integer.class, email);
            return balance != null ? balance : 0;
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Pengguna dengan email " + email + " tidak ditemukan");
        }
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Tidak ada autentikasi yang ditemukan atau pengguna tidak terautentikasi");
            throw new RuntimeException("Tidak ada autentikasi yang ditemukan atau pengguna tidak terautentikasi");
        }

        Object principal = authentication.getPrincipal();
        logger.info("Tipe principal: " + principal.getClass().getName());

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            logger.info("Email dari UserDetails: " + email);
            return email;
        } else if (principal instanceof String) {
            logger.info("Email dari String principal: " + principal);
            return (String) principal;
        } else {
            logger.error("Tipe principal tidak dikenali: " + principal.getClass().getName());
            throw new RuntimeException("Tipe principal tidak dikenali: " + principal.getClass().getName());
        }
    }

    @Override
    @Transactional
    public String topUp(int amount) {
        // Validasi amount
        if (amount <= 0) {
            throw new IllegalArgumentException("Jumlah top-up harus lebih besar dari 0");
        }

        String email = getCurrentUserEmail();
        logger.info("Melakukan top up untuk email: " + email + " dengan jumlah: " + amount);

        String updateBalanceSql = "UPDATE registration SET balance = balance + ? WHERE email = ?";
        String insertTransactionSql = "INSERT INTO transaction (transaction_type, total_amount, created_at, service_code, email) VALUES (?, ?, ?, ?, ?)";
        int oldBalance = getBalance();
        logger.info("Saldo sebelum top up: " + oldBalance);

        try {
            int updatedRows = jdbcTemplate.update(updateBalanceSql, amount, email);
            logger.info("Jumlah baris yang diperbarui saat top up: " + updatedRows);

            if (updatedRows == 0) {
                throw new RuntimeException(
                        "Gagal memperbarui saldo. Pengguna dengan email " + email + " tidak ditemukan.");
            }

            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertTransactionSql,
                        java.sql.Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, "TOPUP");
                ps.setInt(2, amount);
                ps.setTimestamp(3, currentTimestamp);
                ps.setString(4, null);
                ps.setString(5, email);
                return ps;
            }, keyHolder);

           

            int newBalance = getBalance();
            logger.info("Saldo baru setelah top up: " + newBalance);

            return "Top up berhasil. Saldo Anda sekarang: " + newBalance;
        } catch (IllegalArgumentException e) {
            logger.error("Validasi gagal saat melakukan top up", e);
            throw e;
        } catch (Exception e) {
            logger.error("Kesalahan saat melakukan top up", e);
            throw new RuntimeException("Gagal melakukan top up: " + e.getMessage(), e);
        }
    }

    // ... kode lainnya tetap sama ...

    @Override
    @Transactional
    public Map<String, Object> payment(PaymentRequestDto paymentRequest) {
        String email = getCurrentUserEmail();
        String serviceCode = paymentRequest.getServiceCode();

        String getServiceSql = "SELECT service_name, service_tarif FROM services WHERE service_code = ?";
        String updateBalanceSql = "UPDATE registration SET balance = balance - ? WHERE email = ?";
        String insertTransactionSql = "INSERT INTO transaction (service_code, transaction_type, total_amount, created_at, email) VALUES (?, ?, ?, ?, ?)";

        try {
            // Ambil informasi layanan dari database
            Map<String, Object> service = jdbcTemplate.queryForMap(getServiceSql, serviceCode);
            String serviceName = (String) service.get("service_name");
            // Ubah ini untuk menangani service_tarif sebagai Integer
            int serviceTarif = ((Number) service.get("service_tarif")).intValue();

            // Periksa saldo pengguna
            int currentBalance = getBalance();
            if (currentBalance < serviceTarif) {
                throw new RuntimeException("Saldo tidak mencukupi untuk melakukan pembayaran");
            }

            // Kurangi saldo pengguna
            int updatedRows = jdbcTemplate.update(updateBalanceSql, serviceTarif, email);
            if (updatedRows == 0) {
                throw new RuntimeException("Gagal memperbarui saldo");
            }

            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertTransactionSql,
                        java.sql.Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, serviceCode);
                ps.setString(2, "PAYMENT");
                ps.setInt(3, serviceTarif);
                ps.setTimestamp(4, currentTimestamp);
                ps.setString(5, email);
                return ps;
            }, keyHolder);

            Long transactionId = keyHolder.getKey().longValue();
            String invoiceNumber = String.format("INV%tY%<tm%<td-%03d", currentTimestamp, transactionId);

            // Buat hasil dengan urutan yang diinginkan
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("invoice_number", invoiceNumber);
            result.put("transaction_type", "PAYMENT");
            result.put("description", serviceName);
            result.put("total_amount", serviceTarif);
            result.put("created_on", currentTimestamp.toInstant().toString());

            return result;
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Kode layanan tidak ditemukan: " + serviceCode);
        } catch (Exception e) {
            logger.error("Kesalahan saat melakukan pembayaran", e);
            throw new RuntimeException("Gagal melakukan pembayaran: " + e.getMessage());
        }
    }

    // ... kode lainnya tetap sama ..

    @Override
    public List<Map<String, Object>> getTransactionHistory(Integer limit, Integer offset) {
        String email = getCurrentUserEmail();
        String sql = "SELECT t.id, t.transaction_type, t.total_amount, t.created_at, " +
                "CASE WHEN t.transaction_type = 'TOPUP' THEN 'Top Up balance' " +
                "ELSE COALESCE(s.service_name, '') END AS description " +
                "FROM transaction t " +
                "LEFT JOIN services s ON t.service_code = s.service_code " +
                "WHERE t.email = ? " +
                "ORDER BY t.created_at DESC";

        if (limit != null && limit > 0) {
            sql += " LIMIT ?";
            if (offset != null && offset > 0) {
                sql += " OFFSET ?";
            }
        }

        List<Map<String, Object>> rawResults;
        if (limit != null && limit > 0) {
            if (offset != null && offset > 0) {
                rawResults = jdbcTemplate.queryForList(sql, email, limit, offset);
            } else {
                rawResults = jdbcTemplate.queryForList(sql, email, limit);
            }
        } else {
            rawResults = jdbcTemplate.queryForList(sql, email);
        }

        List<Map<String, Object>> formattedResults = new ArrayList<>();
        for (Map<String, Object> row : rawResults) {
            Map<String, Object> formattedRow = new LinkedHashMap<>();
            formattedRow.put("invoice_number",
                    String.format("INV%tY%<tm%<td-%03d", row.get("created_at"), row.get("id")));
            formattedRow.put("transaction_type", row.get("transaction_type"));
            formattedRow.put("description", row.get("description"));
            formattedRow.put("total_amount", row.get("total_amount"));

            // Object createdAt = row.get("created_at");
            // String formattedDate;
            // if (createdAt instanceof java.sql.Date) {
            //     formattedDate = ((java.sql.Date) createdAt).toInstant().toString();
            // } else if (createdAt instanceof java.sql.Timestamp) {
            //     formattedDate = ((java.sql.Timestamp) createdAt).toInstant().toString();
            // } else {
            //     formattedDate = createdAt.toString();
            // }
            // formattedRow.put("created_on", formattedDate);
            formattedRow.put("created_on", row.get("created_at"));

            formattedResults.add(formattedRow);
        }

        return formattedResults;
    }

   

}
