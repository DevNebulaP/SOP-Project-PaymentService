package com.sopproject.paymentservice.controllers;

import com.sopproject.paymentservice.models.Order;
import com.sopproject.paymentservice.services.PayPalPaymentService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.sopproject.paymentservice.services.PayPalPaymentService.CANCEL_URL;
import static com.sopproject.paymentservice.services.PayPalPaymentService.SUCCESS_URL;

@RestController
public class PayPalPaymentController {

    @Autowired
    private PayPalPaymentService service;

    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String payment(@RequestBody Order order) {
        try {
            Payment payment = service.createPayment(order.getPrice(), order.getCurrency(), order.getDescription());
            for(Links link:payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    return "redirect:"+link.getHref();
                }
            }

        } catch (PayPalRESTException e) {

            e.printStackTrace();
        }
        return "redirect:/";
    }

    @RequestMapping(value = CANCEL_URL, method = RequestMethod.GET)
    public String cancelPay() {
        return "cancel";
    }

    @RequestMapping(value = SUCCESS_URL, method = RequestMethod.GET)
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = service.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                return "success";
            }
        } catch (PayPalRESTException e) {
        }
        return "redirect:/";
    }
}