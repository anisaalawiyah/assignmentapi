package com.anisaalawiyah.assignmentapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.anisaalawiyah.assignmentapi.dto.LoginRequestDto;
import com.anisaalawiyah.assignmentapi.dto.LoginResponseDto;
import com.anisaalawiyah.assignmentapi.dto.NameRequestDto;
import com.anisaalawiyah.assignmentapi.dto.RegistrationRequestDto;
import com.anisaalawiyah.assignmentapi.services.AuthService;
import com.anisaalawiyah.assignmentapi.services.RegistrationServices;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@RestController
@ApiOperation(value = "Modul membership", position = 1)

@RequestMapping("/")
@Tag(name = "1.Modul membership")
public class RegistrationController {

    @Autowired
    private RegistrationServices registrationServices;
    
  
    @Autowired
    private AuthService authService;

    @PostMapping("registration")
    public ResponseEntity<?> addRegistration(@RequestBody RegistrationRequestDto request) {
        String result = registrationServices.addRegistration(request);
        if (result.startsWith("Registrasi berhasil")) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", 0);
            response.put("message", "Registrasi berhasil silahkan login");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", 1);
            response.put("message", result);
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto response = authService.login(loginRequest);
        if (response.getToken() != null) {
            Map<String, Object> successResponse = new LinkedHashMap<>();
            successResponse.put("status", 0);
            successResponse.put("message", response.getMessage());
            successResponse.put("data", Map.of("token", response.getToken()));
            return ResponseEntity.ok(successResponse);
        } else {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", 1);
            errorResponse.put("message", response.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    


    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            Map<String, Object> userProfile = registrationServices.fetchUserProfile(); // Gunakan metode baru
            return ResponseEntity.ok(userProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("profile/update")
    public ResponseEntity<?> updateProfile(@RequestBody NameRequestDto nameRequest) {
        String result = registrationServices.updateProfile(nameRequest.getFirstName(), nameRequest.getLastName());
        if (result.startsWith("Profil berhasil diperbarui")) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", 0);
            response.put("message", result);
            response.put("data", null);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", 1);
            response.put("message", result);
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(value="profile/image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfilePhoto(@RequestParam("file") MultipartFile file) {
        try {
            String result = registrationServices.updateProfilePhoto(file);
            if (result.startsWith("Foto profil berhasil")) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("status", 0);
                response.put("message", result);
                response.put("data", null);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("status", 1);
                response.put("message", result);
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", 1);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    
}
