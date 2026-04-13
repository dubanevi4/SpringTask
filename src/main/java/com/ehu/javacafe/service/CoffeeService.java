package com.ehu.javacafe.service;

import com.ehu.javacafe.entity.Beverage;
import com.ehu.javacafe.repository.BeverageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoffeeService {
    private static final Logger logger = LoggerFactory.getLogger(CoffeeService.class);
    private final BeverageRepository beverageRepository;

    public CoffeeService(BeverageRepository beverageRepository) {
        this.beverageRepository = beverageRepository;
    }


    public Beverage getBeverageById(long id) {
        logger.info("Service: Fetching beverage by ID: {}", id);
        Beverage beverage = getAllBeverages().stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);
        if (beverage != null) {
            logger.debug("Service: Found beverage: {}", beverage.getName());
        } else {
            logger.warn("Service: Beverage with ID {} not found", id);
        }
        return beverage;
    }

    public List<Beverage> getAllBeverages() {
        logger.info("Service: Fetching all beverages");
        List<Beverage> beverages = beverageRepository.getAllBeverages();
        logger.debug("Service: Retrieved {} beverages", beverages.size());
        return beverages;
    }

    public long getBeverageCount() {
        return beverageRepository.getBeverageCount();
    }
}
