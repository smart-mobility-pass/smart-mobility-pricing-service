package com.smart.mobility.smartmobilitypricingservice.repository;

import com.smart.mobility.smartmobilitypricingservice.model.PricingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricingResultRepository extends JpaRepository<PricingResult, Long> {
    Optional<PricingResult> findByTripId(Long tripId);
}
