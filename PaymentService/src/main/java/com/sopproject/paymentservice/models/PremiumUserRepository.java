package com.sopproject.paymentservice.models;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PremiumUserRepository extends JpaRepository<PremiumUserEntity, String> {
    PremiumUserEntity findByUserId(String userId);
}
