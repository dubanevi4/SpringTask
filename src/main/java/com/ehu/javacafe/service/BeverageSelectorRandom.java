package com.ehu.javacafe.service;


import com.ehu.javacafe.entity.Beverage;
import com.ehu.javacafe.repository.BeverageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Scope("prototype")
public class BeverageSelectorRandom implements BeverageSelector {
    private final int amountOfBeverages = ThreadLocalRandom.current().nextInt(1, 5);
    private final BeverageRepository beverageRepository;
    private final BeverageSelectionStatsService beverageSelectionStatsService;

    @Autowired
    public BeverageSelectorRandom(BeverageRepository beverageRepository, BeverageSelectionStatsService beverageSelectionStatsService) {
        this.beverageRepository = beverageRepository;
        this.beverageSelectionStatsService = beverageSelectionStatsService;
    }

    @Override
    public List<Beverage> selectBeverage() {
        long beverageCount = beverageRepository.getBeverageCount();
        List<Beverage> order = new ArrayList<>();
        for (int i = 0; i < amountOfBeverages; i++) {
            long randomId = ThreadLocalRandom.current().nextLong(1, beverageCount + 1);
            Beverage beverageById = beverageRepository.getBeverageById(randomId);
            order.add(beverageById);
        }
        beverageSelectionStatsService.recordSelection(order);
        return order;
    }
}
