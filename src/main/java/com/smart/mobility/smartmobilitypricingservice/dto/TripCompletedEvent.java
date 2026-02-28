package com.smart.mobility.smartmobilitypricingservice.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record TripCompletedEvent(
                Long tripId,
                String userId,
                String transportType,
                Integer numberOfSections,
                Integer startZone,
                Integer endZone,
                LocalDateTime startTime,
                LocalDateTime endTime) {
}
