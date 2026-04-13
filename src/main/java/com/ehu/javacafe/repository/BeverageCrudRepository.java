package com.ehu.javacafe.repository;

import com.ehu.javacafe.entity.Beverage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeverageCrudRepository extends CrudRepository<Beverage, Long> {
}

