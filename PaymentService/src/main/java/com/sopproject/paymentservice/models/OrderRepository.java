package com.sopproject.paymentservice.models;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    OrderEntity findByPayId(String payId);
}
