package com.anisaalawiyah.assignmentapi.entities;

import java.sql.Blob;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class services {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "service_code", updatable = false, nullable = false)
    private String service_code;
 
    @Column(name = "service_name")
    private String service_name;
    
    @Column(name = "service_icon")
    private Blob service_icon;

    @Column(name = "service_tarif")
    private int service_tarif;
    
}
