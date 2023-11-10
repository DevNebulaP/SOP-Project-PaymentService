package com.sopproject.paymentservice.models;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class PaymentRequest {
    private String description;
    private String orderItem;
    private String userId;
}