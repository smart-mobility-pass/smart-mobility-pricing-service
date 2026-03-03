package com.smart.mobility.smartmobilitypricingservice.dto;

import com.smart.mobility.smartmobilitypricingservice.enums.PassType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingContextDTO {
    private boolean hasActivePass;
    private PassType passType;
    private Double dailyCapAmount;
    private List<SubscriptionContextDTO> activeSubscriptions;
}
