package com.smart.mobility.smartmobilitypricingservice.controller;

import com.smart.mobility.smartmobilitypricingservice.dto.PricingResponseDTO;
import com.smart.mobility.smartmobilitypricingservice.dto.TripCompletedEvent;
import com.smart.mobility.smartmobilitypricingservice.model.PricingResult;
import com.smart.mobility.smartmobilitypricingservice.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping("/calculate")
    public ResponseEntity<PricingResponseDTO> calculatePrice(@RequestBody TripCompletedEvent event) {
        return ResponseEntity.ok(pricingService.calculateAndProcessTrip(event));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<PricingResult> getPricingByTripId(@PathVariable Long tripId) {
        PricingResult result = pricingService.getPricingByTripId(tripId);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }
}
