package com.smart.mobility.smartmobilitypricingservice.controller;

import com.smart.mobility.smartmobilitypricingservice.dto.PricingResponseDTO;
import com.smart.mobility.smartmobilitypricingservice.dto.TripCompletedEvent;
import com.smart.mobility.smartmobilitypricingservice.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping("/calculate")
    public ResponseEntity<PricingResponseDTO> calculatePrice(@RequestBody TripCompletedEvent event) {
        return ResponseEntity.ok(pricingService.calculateAndProcessTrip(event));
    }
}
