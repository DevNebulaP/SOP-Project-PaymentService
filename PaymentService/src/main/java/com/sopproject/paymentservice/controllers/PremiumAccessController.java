package com.sopproject.paymentservice.controllers;

import com.sopproject.paymentservice.models.PremiumUserEntity;
import com.sopproject.paymentservice.models.PremiumUserRepository;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
public class PremiumAccessController {

    private final PremiumUserRepository premiumUserRepository;

    public PremiumAccessController(PremiumUserRepository premiumUserRepository){
        this.premiumUserRepository = premiumUserRepository;
    }
    @RequestMapping(value = "/checkPremium/{userId}", method = RequestMethod.GET)
    private boolean checkPremium(@PathVariable("userId") String userId){
        PremiumUserEntity premiumUserEntity = premiumUserRepository.findByUserId(userId);
        if(premiumUserEntity == null){
            return false;
        }
        return LocalDate.now().isBefore(premiumUserEntity.getPremiumCoverage().toLocalDate());
    }

    @RequestMapping(value = "/getPremiumCoverage/{userId}", method = RequestMethod.GET)
    private LocalDateTime getPremiumCoverage(@PathVariable("userId") String userId){
        PremiumUserEntity premiumUserEntity = premiumUserRepository.findByUserId(userId);
        if(premiumUserEntity == null){
            return null;
        }
        return premiumUserEntity.getPremiumCoverage();
    }
}
