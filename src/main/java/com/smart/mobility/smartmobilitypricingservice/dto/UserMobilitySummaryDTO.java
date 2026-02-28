package com.smart.mobility.smartmobilitypricingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMobilitySummaryDTO {
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private boolean hasActivePass;
    private String passType;
    private String passStatus;
    private Double dailyCap;
    private Double currentSpent;
    private Double activeDiscountRate;
}
