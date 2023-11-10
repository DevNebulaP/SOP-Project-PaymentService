package com.sopproject.paymentservice.models;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class Order {
    private double price;
    private String currency;
    private String description;
    private String orderItem;
}