package com.anisaalawiyah.assignmentapi.services;

import com.anisaalawiyah.assignmentapi.dto.RegistrationRequestDto;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface RegistrationServices {
    String addRegistration(RegistrationRequestDto request);
    // String updateNames(String id, String firstName, String lastName);
    String addProfilePhoto(String id, MultipartFile file);
    String updateProfile(String firstName, String lastName);
    String updateProfilePhoto(MultipartFile file);
    Map<String, Object> getUserProfile();
    Map<String, Object> fetchUserProfile(); 
} 
    




