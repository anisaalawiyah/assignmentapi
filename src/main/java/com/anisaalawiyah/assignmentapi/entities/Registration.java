package com.anisaalawiyah.assignmentapi.entities;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Registration{
    @Id
     @Column(name = "id", nullable = false)
    private String id;
    
    @Column(name = "email")
    private String email;

    @Column(name = "fist_name")
    private String fist_name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "password")
    private String password;
   
    @Lob
    @Column(name = "image_photo")
    @JsonIgnore
    private Blob image_photo;

    @JsonProperty("image_photo")
    public String getPhotoBase64() throws SQLException {
    if (image_photo != null)
            return new String(Base64.getEncoder().encode(image_photo.getBytes(1L, (int) image_photo.length())));
        return null;
    }

    @Column(name = "balance")
    private int balance =0;


}