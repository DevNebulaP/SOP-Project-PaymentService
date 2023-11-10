package com.sopproject.paymentservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Data
public class OrderEntity implements Serializable {

    @Id
    @Column(unique = true)
    private String payId;
    private String payerId;
    private String userId;
    private String orderItem;
    private BigDecimal price;
    private String currency;
    private OrderStatus orderStatus;
}