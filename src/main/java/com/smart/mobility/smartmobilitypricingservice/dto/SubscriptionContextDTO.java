package com.smart.mobility.smartmobilitypricingservice.dto;

import com.smart.mobility.smartmobilitypricingservice.enums.TransportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionContextDTO {
    private TransportType applicableTransport;
    private Double discountPercentage;
}
