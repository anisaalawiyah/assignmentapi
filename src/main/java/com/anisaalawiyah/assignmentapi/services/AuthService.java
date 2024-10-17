package com.anisaalawiyah.assignmentapi.services;

import com.anisaalawiyah.assignmentapi.dto.LoginRequestDto;
import com.anisaalawiyah.assignmentapi.dto.LoginResponseDto;
import com.anisaalawiyah.assignmentapi.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponseDto login(LoginRequestDto loginRequest) {
        String sql = "SELECT password FROM registration WHERE LOWER(email) = LOWER(?)";
        try {
            String storedPassword = jdbcTemplate.queryForObject(sql, String.class, loginRequest.getEmail().trim());
            
            logger.info("Password tersimpan (terenkripsi): {}", storedPassword);
            logger.info("Password input: {}", loginRequest.getPassword());
            
            if (passwordEncoder.matches(loginRequest.getPassword().trim(), storedPassword)) {
                String token = jwtUtil.generateToken(loginRequest.getEmail().trim());
                logger.info("Login berhasil untuk email: {}", loginRequest.getEmail());
                return new LoginResponseDto("Login berhasil", token);
            } else {
                logger.warn("Password tidak cocok untuk email: {}", loginRequest.getEmail());
                return new LoginResponseDto("Email atau password salah", null);
            }
        } catch (EmptyResultDataAccessException e) {
            logger.warn("User tidak ditemukan untuk email: {}", loginRequest.getEmail());
            return new LoginResponseDto("Email atau password salah", null);
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat login: ", e);
            return new LoginResponseDto("Terjadi kesalahan saat login", null);
        }
    }
}
