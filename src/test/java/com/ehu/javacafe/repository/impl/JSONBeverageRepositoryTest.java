package com.ehu.javacafe.repository.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONBeverageRepositoryTest {

    @Test
    void loadsBeveragesFromClasspathJson() {
        JSONBeverageRepository repo = new JSONBeverageRepository();

        assertNotNull(repo.getAllBeverages());
        assertFalse(repo.getAllBeverages().isEmpty(), "beverages.json should provide at least one beverage");
        assertEquals(repo.getAllBeverages().size(), repo.getBeverageCount());
    }
}
