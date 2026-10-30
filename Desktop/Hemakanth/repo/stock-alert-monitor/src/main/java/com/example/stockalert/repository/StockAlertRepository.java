package com.example.stockalert.repository;

import com.example.stockalert.entity.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {

    List<StockAlert> findByEnabledTrue();
}
