package com.sopproject.paymentservice.controllers;

import com.sopproject.paymentservice.models.Order;
import com.sopproject.paymentservice.models.OrderEntity;
import com.sopproject.paymentservice.models.OrderRepository;
import com.sopproject.paymentservice.models.OrderStatus;
import com.sopproject.paymentservice.services.PayPalPaymentService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class PayPalPaymentController {

    @Autowired
    private PayPalPaymentService service;

    private final OrderRepository orderRepository;

    public PayPalPaymentController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String payment(@RequestBody Order order) {
        try {
            Payment payment = service.createPayment(order.getPrice(), order.getCurrency(), order.getDescription());
            for(Links link:payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    OrderEntity entity = new OrderEntity();
                    entity.setPayId(payment.getId());
                    entity.setPrice(BigDecimal.valueOf(order.getPrice()));
                    entity.setCurrency(order.getCurrency());
                    entity.setOrderItem(order.getOrderItem());
                    entity.setOrderStatus(OrderStatus.CREATED);
                    orderRepository.save(entity);
                    return "redirect:"+link.getHref();
                }
            }

        } catch (PayPalRESTException e) {

            e.printStackTrace();
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    public String cancelPay() {
        return "cancel";
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            OrderEntity entity = orderRepository.findByPayId(paymentId);
            if(entity == null) {
                return "error: Order matching paymentId not found.";
            }
            Payment payment = service.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                entity.setOrderStatus(OrderStatus.PAID);
                entity.setPayerId(payerId);
                orderRepository.save(entity);
                return "success";
            }
        } catch (PayPalRESTException e) {
        }
        return "redirect:/";
    }
}