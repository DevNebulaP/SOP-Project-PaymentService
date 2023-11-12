package com.sopproject.paymentservice.controllers;

import com.sopproject.paymentservice.models.PremiumUserEntity;
import com.sopproject.paymentservice.models.PremiumUserRepository;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@CrossOrigin(origins = "*", allowedHeaders = "*")
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
