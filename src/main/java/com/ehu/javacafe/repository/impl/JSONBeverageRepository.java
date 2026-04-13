package com.ehu.javacafe.repository.impl;

import com.ehu.javacafe.entity.Beverage;
import com.ehu.javacafe.repository.BeverageRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Primary
@Repository
public class JSONBeverageRepository implements BeverageRepository {
    private static final Logger logger = LoggerFactory.getLogger(JSONBeverageRepository.class);
    private static final String BEVERAGES_FILE = "beverages.json";
    private final List<Beverage> beverages;

    public JSONBeverageRepository() {
        this.beverages = loadBeveragesFromJson();
    }

    private List<Beverage> loadBeveragesFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(BEVERAGES_FILE)) {
            if (inputStream == null) {
                logger.error("Could not find {} in resources", BEVERAGES_FILE);
                throw new RuntimeException("Could not find " + BEVERAGES_FILE + " in resources");
            }
            List<Beverage> loadedBeverages = new ArrayList<>(objectMapper.readValue(inputStream, new TypeReference<List<Beverage>>() {}));
            logger.info("Successfully loaded {} beverages from JSON file", loadedBeverages.size());
            return loadedBeverages;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load beverages from JSON file", e);
        }
    }

    @Override
    public List<Beverage> getAllBeverages() {
        logger.info("Fetching all beverages from JSON repository");
        logger.debug("Total beverages available: {}", beverages.size());
        return beverages;
    }

    @Override
    public boolean addBeverage(Beverage beverage) {
        return beverages.add(beverage);
    }

    @Override
    public Beverage deleteBeverage(long id) {
        Beverage beverageToDelete = getBeverageById(id);
        if (beverageToDelete != null) {
            beverages.remove(beverageToDelete);
        } else {
            logger.warn("Beverage with ID {} not found, cannot delete", id);
        }
        return beverageToDelete;
    }

    @Override
    public Beverage getBeverageById(long id) {
        logger.debug("Fetching beverage by ID: {}", id);
        return beverages.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public long getBeverageCount() {
        return beverages.size();
    }
}
