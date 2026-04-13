package com.ehu.javacafe.repository.impl;

import com.ehu.javacafe.entity.Beverage;
import com.ehu.javacafe.repository.BeverageCrudRepository;
import com.ehu.javacafe.repository.BeverageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DBBeverageRepository implements BeverageRepository {
    private static final Logger logger = LoggerFactory.getLogger(DBBeverageRepository.class);
    private final BeverageCrudRepository beverageCrudRepository;

    @Autowired
    public DBBeverageRepository(BeverageCrudRepository beverageCrudRepository) {
        this.beverageCrudRepository = beverageCrudRepository;
        logger.info("DBBeverageRepository initialized with Spring Data JDBC");
    }

    @Override
    public List<Beverage> getAllBeverages() {
        logger.info("Fetching all beverages from H2 database");
        List<Beverage> beverages = new ArrayList<>();
        beverageCrudRepository.findAll().forEach(beverages::add);
        logger.debug("Retrieved {} beverages from database", beverages.size());
        return beverages;
    }

    @Override
    public boolean addBeverage(Beverage beverage) {
        logger.info("Adding new beverage to database: {} (Price: ${})", beverage.getName(), beverage.getPrice());
        try {
            Beverage saved = beverageCrudRepository.save(beverage);
            logger.info("Beverage saved successfully with ID: {}", saved.getId());
            return true;
        } catch (Exception e) {
            logger.error("Failed to add beverage: {}", beverage.getName(), e);
            return false;
        }
    }

    @Override
    public Beverage deleteBeverage(long id) {
        logger.info("Attempting to delete beverage with ID: {}", id);
        Optional<Beverage> beverageOptional = beverageCrudRepository.findById(id);

        if (beverageOptional.isPresent()) {
            Beverage beverage = beverageOptional.get();
            beverageCrudRepository.deleteById(id);
            logger.info("Successfully deleted beverage: {} (ID: {})", beverage.getName(), id);
            return beverage;
        } else {
            logger.warn("Beverage with ID {} not found in database, cannot delete", id);
            return null;
        }
    }

    @Override
    public Beverage getBeverageById(long id) {
        logger.debug("Fetching beverage by ID: {} from database", id);
        Optional<Beverage> beverage = beverageCrudRepository.findById(id);

        if (beverage.isPresent()) {
            logger.debug("Found beverage: {} (ID: {})", beverage.get().getName(), id);
            return beverage.get();
        } else {
            logger.debug("Beverage with ID {} not found in database", id);
            return null;
        }
    }

    @Override
    public long getBeverageCount() {
        return beverageCrudRepository.count();
    }
}
