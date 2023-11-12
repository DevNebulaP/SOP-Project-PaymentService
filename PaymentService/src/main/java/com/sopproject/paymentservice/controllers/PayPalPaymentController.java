package com.sopproject.paymentservice.controllers;

import com.sopproject.paymentservice.models.*;
import com.sopproject.paymentservice.services.PayPalPaymentService;
import com.paypal.api.payments.Links;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PayPalPaymentController {

    @Autowired
    private PayPalPaymentService service;

    private final OrderRepository orderRepository;
    private final PremiumUserRepository premiumUserRepository;

    public PayPalPaymentController(OrderRepository orderRepository, PremiumUserRepository premiumUserRepository) {
        this.orderRepository = orderRepository;
        this.premiumUserRepository = premiumUserRepository;
    }

    @RequestMapping(value = "/payPremium", method = RequestMethod.POST)
    public String payment(@RequestBody PaymentRequest paymentRequest) {
        if(paymentRequest.getUserId() == null || paymentRequest.getOrderItem() == null){
            throw new IllegalArgumentException("Bad request");
        }
        try {
            com.paypal.api.payments.Payment payment = service.createPayment(2.99, "USD", paymentRequest.getDescription());
            for(Links link:payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    OrderEntity entity = new OrderEntity();
                    entity.setPayId(payment.getId());
                    entity.setPrice(BigDecimal.valueOf(2.99));
                    entity.setCurrency("USD");
                    entity.setOrderItem(paymentRequest.getOrderItem());
                    entity.setOrderStatus(OrderStatus.CREATED);
                    entity.setUserId(paymentRequest.getUserId());
                    orderRepository.save(entity);
                    return "redirect:"+link.getHref();
                }
            }

        } catch (PayPalRESTException e) {

            e.printStackTrace();
        }
        return "error";
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    public String cancelPay() {
        return "Payment Cancelled";
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            OrderEntity entity = orderRepository.findByPayId(paymentId);
            if(entity == null) {
                return "error: Order matching paymentId not found.";
            }
            com.paypal.api.payments.Payment payment = service.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                entity.setOrderStatus(OrderStatus.PAID);
                entity.setPayerId(payerId);
                entity.setPaidDateTime(LocalDateTime.now());
                orderRepository.save(entity);

                PremiumUserEntity premiumUserEntity = premiumUserRepository.findByUserId(entity.getUserId());
                if(premiumUserEntity == null){
                    premiumUserEntity = new PremiumUserEntity();
                    premiumUserEntity.setUserId(entity.getUserId());
                    premiumUserEntity.setPremiumCoverage(LocalDateTime.now());
                }
                if(premiumUserEntity.getPremiumCoverage().isBefore(entity.getPaidDateTime())){
                    premiumUserEntity.setPremiumCoverage(LocalDateTime.now().plusDays(30));
                }
                else{
                    premiumUserEntity.setPremiumCoverage(premiumUserEntity.getPremiumCoverage().plusDays(30));
                }
                premiumUserRepository.save(premiumUserEntity);

                return "success";
            }
        } catch (PayPalRESTException e) {
        }
        return "error";
    }
}