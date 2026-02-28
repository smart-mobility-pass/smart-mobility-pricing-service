package com.smart.mobility.smartmobilitypricingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingResponseDTO {
    private BigDecimal basePrice;
    private BigDecimal discountApplied;
    private BigDecimal finalPrice;
    private boolean capReached;
}
