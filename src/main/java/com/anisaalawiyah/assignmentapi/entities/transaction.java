package com.anisaalawiyah.assignmentapi.entities;

import java.sql.Date;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "transaction_type")
    private String transaction_type;

    @Column(name = "total_amount")
    private int total_amount;

    @Column(name = "created_at")
    private Date created_at;

    @Column(name = "email")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_code", referencedColumnName = "service_code")
    private services service;
    // Getter untuk descriptio
    public String getDescription() {
        return service != null ? service.getService_code() : null;
    }
}
