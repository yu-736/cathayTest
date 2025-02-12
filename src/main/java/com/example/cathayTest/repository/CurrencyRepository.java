package com.example.cathayTest.repository;

import com.example.cathayTest.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 幣別資料 Repository
 */
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, String> {
}
