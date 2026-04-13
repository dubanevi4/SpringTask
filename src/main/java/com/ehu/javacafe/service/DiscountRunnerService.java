package com.ehu.javacafe.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class DiscountRunnerService {
    private final DailyBeverageDiscountService dailyBeverageDiscountService;

    public DiscountRunnerService(@Lazy DailyBeverageDiscountService dailyBeverageDiscountService) {
        this.dailyBeverageDiscountService = dailyBeverageDiscountService;
    }

    public int fetchDiscountPercent() {
        return dailyBeverageDiscountService.getTodayDiscountPercent();
    }
}
