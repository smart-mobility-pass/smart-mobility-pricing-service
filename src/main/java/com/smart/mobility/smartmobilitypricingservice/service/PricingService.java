package com.smart.mobility.smartmobilitypricingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.mobility.smartmobilitypricingservice.dto.AppliedDiscountDto;
import com.smart.mobility.smartmobilitypricingservice.model.DiscountRule;
import com.smart.mobility.smartmobilitypricingservice.model.PricingResult;
import com.smart.mobility.smartmobilitypricingservice.dto.TripCompletedEvent;
import com.smart.mobility.smartmobilitypricingservice.dto.TripPricedEvent;
import com.smart.mobility.smartmobilitypricingservice.repository.DiscountRuleRepository;
import com.smart.mobility.smartmobilitypricingservice.repository.PricingResultRepository;
import com.smart.mobility.smartmobilitypricingservice.proxy.UserServiceClient;
import com.smart.mobility.smartmobilitypricingservice.messaging.PricingEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final DiscountRuleRepository discountRuleRepository;
    private final PricingResultRepository pricingResultRepository;
    private final UserServiceClient userServiceClient;
    private final PricingEventPublisher pricingEventPublisher; // We will create this interface/class later
    private final ObjectMapper objectMapper;

    @Transactional
    public void processTripCompleted(TripCompletedEvent event) {
        log.info("Processing pricing for trip {}", event.tripId());

        // 1. Calculate Base Price
        BigDecimal basePrice = calculateBasePrice(event);

        // 2. Fetch User Subscription
        String subscriptionType = fetchUserSubscription(event.userId());

        // 3. Apply Discounts
        List<AppliedDiscountDto> appliedDiscounts = new ArrayList<>();
        BigDecimal finalAmount = applyDiscounts(basePrice, subscriptionType, appliedDiscounts);
        BigDecimal totalDiscountApplied = basePrice.subtract(finalAmount);

        // 4. Save Pricing Result
        String appliedDiscountsJson = "[]";
        try {
            appliedDiscountsJson = objectMapper.writeValueAsString(appliedDiscounts);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize applied discounts", e);
        }

        PricingResult pricingResult = PricingResult.builder()
                .tripId(event.tripId())
                .userId(event.userId())
                .transportType(event.transportType())
                .basePrice(basePrice)
                .discountApplied(totalDiscountApplied)
                .finalAmount(finalAmount)
                .appliedDiscounts(appliedDiscountsJson)
                .build();

        pricingResult = pricingResultRepository.save(pricingResult);

        // 5. Publish Event
        TripPricedEvent pricedEvent = TripPricedEvent.builder()
                .tripId(event.tripId())
                .userId(event.userId())
                .basePrice(basePrice)
                .appliedDiscounts(appliedDiscounts)
                .finalAmount(finalAmount)
                .build();

        pricingEventPublisher.publishTripPricedEvent(pricedEvent);
        log.info("Finished pricing for trip {}. Final amount: {}", event.tripId(), finalAmount);
    }

    private BigDecimal calculateBasePrice(TripCompletedEvent event) {
        int numberSection = event.numberOfSections() != null ? event.numberOfSections() : 0;
        if ("BUS".equalsIgnoreCase(event.transportType())) {
            // formula: 150 + (numberOfSections * 50)
            return BigDecimal.valueOf(150 + (numberSection * 50L));
        } else if ("TER".equalsIgnoreCase(event.transportType())) {
            // formula: 500 + (numberOfSections * 500)
            return BigDecimal.valueOf(500 + (numberSection * 500L));
        } else if ("BRT".equalsIgnoreCase(event.transportType())) {
            // formula: startZone == endZone ? 400 : 500
            int startZone = event.startZone() != null ? event.startZone() : -1;
            int endZone = event.endZone() != null ? event.endZone() : -1;
            if (startZone == endZone && startZone != -1) {
                return BigDecimal.valueOf(400);
            } else {
                return BigDecimal.valueOf(500);
            }
        }
        return BigDecimal.ZERO;
    }

    private String fetchUserSubscription(Long userId) {
        try {
            return userServiceClient.getActiveSubscription(userId);
        } catch (Exception e) {
            log.warn("Failed to fetch subscription for user {}, continuing without subscription discount", userId);
            return "NONE";
        }
    }

    private BigDecimal applyDiscounts(BigDecimal currentAmount, String subscriptionType,
            List<AppliedDiscountDto> appliedDiscounts) {
        List<DiscountRule> activeRules = discountRuleRepository.findByActiveTrueOrderByPriorityAsc();

        for (DiscountRule rule : activeRules) {
            if ("SUBSCRIPTION".equalsIgnoreCase(rule.getRuleType())) {
                boolean matches = rule.getCondition() != null
                        && rule.getCondition().contains(subscriptionType != null ? subscriptionType : "");
                if (matches) {
                    currentAmount = applyRule(currentAmount, rule, appliedDiscounts);
                }
            } else {
                // For other types like OFFPEAK or LOYALTY, we might need more event context or
                // user details.
                // For this implementation, we apply them if condition is basic or doesn't
                // strictly exclude it.
                // Assuming empty conditions mean applies to all, otherwise matching logic is
                // required.
                if (rule.getCondition() == null || rule.getCondition().isEmpty()
                        || "ALL".equalsIgnoreCase(rule.getCondition())) {
                    currentAmount = applyRule(currentAmount, rule, appliedDiscounts);
                }
            }
        }
        return currentAmount;
    }

    private BigDecimal applyRule(BigDecimal currentAmount, DiscountRule rule,
            List<AppliedDiscountDto> appliedDiscounts) {
        BigDecimal percentage = rule.getPercentage();
        if (percentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountFactor = percentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal discountAmount = currentAmount.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);

            appliedDiscounts.add(AppliedDiscountDto.builder()
                    .ruleType(rule.getRuleType())
                    .percentage(percentage)
                    .amountDeducted(discountAmount)
                    .build());

            return currentAmount.subtract(discountAmount);
        }
        return currentAmount;
    }
}
