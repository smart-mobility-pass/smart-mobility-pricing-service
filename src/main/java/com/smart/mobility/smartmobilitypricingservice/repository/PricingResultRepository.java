package com.smart.mobility.smartmobilitypricingservice.repository;

import com.smart.mobility.smartmobilitypricingservice.model.PricingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingResultRepository extends JpaRepository<PricingResult, Long> {
    List<PricingResult> findByUserId(Long userId);

    List<PricingResult> findByTripId(Long tripId);
}
