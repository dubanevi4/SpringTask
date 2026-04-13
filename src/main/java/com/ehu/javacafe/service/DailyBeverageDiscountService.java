package com.ehu.javacafe.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Lazy
@Service
public class DailyBeverageDiscountService {
    private static final Logger logger = LoggerFactory.getLogger(DailyBeverageDiscountService.class);

    public DailyBeverageDiscountService() {
        logger.info("DailyBeverageDiscountService initialized");
    }

    @PostConstruct
    public void postConstruct() {
        logger.info("DailyBeverageDiscountService @PostConstruct called");
    }

    @PreDestroy
    public void preDestroy() {
        logger.info("DailyBeverageDiscountService @PreDestroy called");
    }

    public int getTodayDiscountPercent() {
        int day = LocalDate.now().getDayOfMonth();
        return day % 15;
    }
}
