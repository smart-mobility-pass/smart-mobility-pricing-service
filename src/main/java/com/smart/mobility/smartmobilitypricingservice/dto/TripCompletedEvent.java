package com.smart.mobility.smartmobilitypricingservice.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record TripCompletedEvent(
                Long tripId,
                String userId,
                String transportType,
                Long transportLineId,
                String startLocation,
                String endLocation,
                LocalDateTime startTime,
                LocalDateTime endTime,
                boolean mismatch) {
}
