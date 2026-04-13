package com.ehu.javacafe.service;

import com.ehu.javacafe.entity.Beverage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@Service
public class BeverageSelectionStatsService {
    private static final Logger logger = LoggerFactory.getLogger(BeverageSelectionStatsService.class);

    private final Map<Long, LongAdder> beverageSelections = new ConcurrentHashMap<>();

    public void recordSelection(List<Beverage> selected) {
        if (selected == null || selected.isEmpty()) {
            return;
        }

        for (Beverage beverage : selected) {
            if (beverage == null) {
                continue;
            }
            beverageSelections.computeIfAbsent(beverage.getId(), id -> new LongAdder()).increment();
        }

        logger.info("Beverage selection stats: {}", snapshot());
    }

    public Map<Long, Long> snapshot() {
        Map<Long, Long> snapshot = new ConcurrentHashMap<>();
        for (Map.Entry<Long, LongAdder> entry : beverageSelections.entrySet()) {
            snapshot.put(entry.getKey(), entry.getValue().sum());
        }
        return snapshot;
    }
}
