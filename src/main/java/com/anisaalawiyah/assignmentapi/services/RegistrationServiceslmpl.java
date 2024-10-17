package com.anisaalawiyah.assignmentapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.anisaalawiyah.assignmentapi.dto.RegistrationRequestDto;


import java.util.UUID;
import java.util.regex.Pattern;

import javax.sql.rowset.serial.SerialBlob;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class RegistrationServiceslmpl implements RegistrationServices {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@gmail\\.com$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    private static final String UPLOAD_DIRECTORY = "./uploads/profile-photos/";
    @Override
    public String addRegistration(RegistrationRequestDto request) {
        // Validasi email
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return "Email tidak boleh kosong";
        }
        if (!isValidEmail(request.getEmail())) {
            return "Format email tidak valid. Gunakan email dengan domain @gmail.com";
        }
        
        // Validasi panjang password
        if (request.getPassword().length() < 8) {
            return "Password harus memiliki minimal 8 karakter";
        }
        
        String sql = "INSERT INTO registration (id, email, fist_name, last_name, password, balance) VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            String id = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(request.getPassword().trim());
            int result = jdbcTemplate.update(sql, 
                id,
                request.getEmail().trim().toLowerCase(), 
                request.getFirs_name().trim(), 
                request.getLast_name().trim(), 
                encodedPassword,
                0  // Menambahkan nilai default 0 untuk balance
            );
            
            if (result > 0) {
                return "Registrasi berhasil untuk " + request.getFirs_name() + " " + request.getLast_name();
            } else {
                return "Registrasi gagal";
            }
        } catch (Exception e) {
            return "Terjadi kesalahan: " + e.getMessage();
        }
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
   
    @Override
    public String addProfilePhoto(String id, MultipartFile file) {
        try {
            String checkSql = "SELECT COUNT(*) FROM registration WHERE id = ?";
            int count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);
            if (count == 0) {
                return "ID tidak ditemukan";
            }

            Files.createDirectories(Paths.get(UPLOAD_DIRECTORY));

            String fileName = id + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIRECTORY + fileName);
            Files.write(filePath, file.getBytes());

            String updateSql = "UPDATE registration SET image_photo = ? WHERE id = ?";
            int result = jdbcTemplate.update(updateSql, filePath.toString(), id);

            if (result > 0) {
                return "Foto profil berhasil ditambahkan untuk ID: " + id;
            } else {
                return "Gagal menambahkan foto profil";
            }
        } catch (Exception e) {
            return "Terjadi kesalahan: " + e.getMessage();
        }
    }

    public void updateExistingPasswords() {
        String sql = "SELECT id, password FROM registration";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);
        
        for (Map<String, Object> user : users) {
            String id = (String) user.get("id");
            String plainPassword = (String) user.get("password");
            String encodedPassword = passwordEncoder.encode(plainPassword);
            
            String updateSql = "UPDATE registration SET password = ? WHERE id = ?";
            jdbcTemplate.update(updateSql, encodedPassword, id);
        }
    }

    @Override
    public String updateProfile(String firstName, String lastName) {
        String sql = "UPDATE registration SET fist_name = ?, last_name = ? WHERE email = ?";
        
        try {
            String email = getCurrentUserEmail();
            
            int result = jdbcTemplate.update(sql, firstName, lastName, email);
            
            if (result > 0) {
                return "Profil berhasil diperbarui untuk " ;
            } else {
                return "Gagal memperbarui profil. Email tidak ditemukan.";
            }
        } catch (Exception e) {
            return "Terjadi kesalahan: " + e.getMessage();
        }
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Pengguna tidak terautentikasi");
        }
        return authentication.getName();
    }

    @Override
    public String updateProfilePhoto(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return "File tidak didukung";
            }
            
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            if (!extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("jpeg") && !extension.equalsIgnoreCase("png")) {
                return "File tidak didukung! (Hanya jpg, jpeg dan png yang diterima)";
            }

            String sql = "SELECT * FROM registration WHERE email = ?";
            Map<String, Object> user = jdbcTemplate.queryForMap(sql, getCurrentUserEmail());
            if (user == null) {
                return "Pengguna tidak ditemukan";
            }

            byte[] bytes = file.getBytes();
            sql = "UPDATE registration SET image_photo = ? WHERE email = ?";
            int updatedRows = jdbcTemplate.update(sql, new SerialBlob(bytes), getCurrentUserEmail());

            if (updatedRows > 0) {
                return "Foto profil berhasil diperbarui";
            } else {
                return "Gagal memperbarui foto profil";
            }
        } catch (Exception e) {
            return "Terjadi kesalahan: " + e.getMessage();
        }
    }
    

    @Override
    public Map<String, Object> getUserProfile() {
        return fetchUserProfile();
    }

    @Override
    public Map<String, Object> fetchUserProfile() {
        String email = getCurrentUserEmail();
        String sql = "SELECT fist_name, last_name, email, image_photo FROM registration WHERE email = ?";
        
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, email);
            
            Map<String, Object> formattedResult = new LinkedHashMap<>();
            formattedResult.put("fist_name", result.get("fist_name"));
            formattedResult.put("last_name", result.get("last_name"));
            formattedResult.put("email", result.get("email"));
            formattedResult.put("image_photo", result.get("image_photo"));
            
            return formattedResult;
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Pengguna dengan email " + email + " tidak ditemukan");
        }
    }

}
