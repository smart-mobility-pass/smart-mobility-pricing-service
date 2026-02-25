package com.smart.mobility.smartmobilitypricingservice.repository;

import com.smart.mobility.smartmobilitypricingservice.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Optional<Zone> findByZoneNumber(Integer zoneNumber);
}
