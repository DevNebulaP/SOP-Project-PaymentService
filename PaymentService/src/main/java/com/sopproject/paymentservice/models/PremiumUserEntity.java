package com.sopproject.paymentservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
@Entity
@Table(name = "premiumUsers")
@Data
public class PremiumUserEntity implements Serializable {
    @Id
    @Column(unique = true)
    private String userId;
    private LocalDateTime premiumCoverage;
}
