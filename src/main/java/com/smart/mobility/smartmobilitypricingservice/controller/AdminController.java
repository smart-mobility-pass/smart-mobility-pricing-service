package com.smart.mobility.smartmobilitypricingservice.controller;

import com.smart.mobility.smartmobilitypricingservice.model.DiscountRule;
import com.smart.mobility.smartmobilitypricingservice.model.FareSection;
import com.smart.mobility.smartmobilitypricingservice.model.TransportLine;
import com.smart.mobility.smartmobilitypricingservice.model.Zone;
import com.smart.mobility.smartmobilitypricingservice.repository.DiscountRuleRepository;
import com.smart.mobility.smartmobilitypricingservice.repository.FareSectionRepository;
import com.smart.mobility.smartmobilitypricingservice.repository.TransportLineRepository;
import com.smart.mobility.smartmobilitypricingservice.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TransportLineRepository transportLineRepository;
    private final FareSectionRepository fareSectionRepository;
    private final ZoneRepository zoneRepository;
    private final DiscountRuleRepository discountRuleRepository;

    // --- Transport Line ---
    @PostMapping("/transport-lines")
    public ResponseEntity<TransportLine> createTransportLine(@RequestBody TransportLine transportLine) {
        return ResponseEntity.ok(transportLineRepository.save(transportLine));
    }

    @GetMapping("/transport-lines")
    public ResponseEntity<List<TransportLine>> getAllTransportLines() {
        return ResponseEntity.ok(transportLineRepository.findAll());
    }

    // --- Fare Section ---
    @PostMapping("/fare-sections")
    public ResponseEntity<FareSection> createFareSection(@RequestBody FareSection fareSection) {
        return ResponseEntity.ok(fareSectionRepository.save(fareSection));
    }

    @GetMapping("/fare-sections")
    public ResponseEntity<List<FareSection>> getAllFareSections() {
        return ResponseEntity.ok(fareSectionRepository.findAll());
    }

    // --- Zone ---
    @PostMapping("/zones")
    public ResponseEntity<Zone> createZone(@RequestBody Zone zone) {
        return ResponseEntity.ok(zoneRepository.save(zone));
    }

    @GetMapping("/zones")
    public ResponseEntity<List<Zone>> getAllZones() {
        return ResponseEntity.ok(zoneRepository.findAll());
    }

    // --- Discount Rule ---
    @PostMapping("/discount-rules")
    public ResponseEntity<DiscountRule> createDiscountRule(@RequestBody DiscountRule discountRule) {
        return ResponseEntity.ok(discountRuleRepository.save(discountRule));
    }

    @GetMapping("/discount-rules")
    public ResponseEntity<List<DiscountRule>> getAllDiscountRules() {
        return ResponseEntity.ok(discountRuleRepository.findAll());
    }

    @DeleteMapping("/discount-rules/{id}")
    public ResponseEntity<Void> deleteDiscountRule(@PathVariable Long id) {
        discountRuleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
