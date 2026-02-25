package com.smart.mobility.smartmobilitypricingservice.repository;

import com.smart.mobility.smartmobilitypricingservice.model.TransportLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransportLineRepository extends JpaRepository<TransportLine, Long> {
    Optional<TransportLine> findByTransportTypeAndName(String transportType, String name);
}
