package com.ehu.javacafe.service;

import com.ehu.javacafe.entity.Beverage;

import java.util.List;

public interface BeverageSelector {
    List<Beverage> selectBeverage();
}
