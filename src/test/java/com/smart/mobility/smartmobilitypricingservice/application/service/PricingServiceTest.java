package com.smart.mobility.smartmobilitypricingservice.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.mobility.smartmobilitypricingservice.model.DiscountRule;
import com.smart.mobility.smartmobilitypricingservice.model.PricingResult;
import com.smart.mobility.smartmobilitypricingservice.dto.TripCompletedEvent;
import com.smart.mobility.smartmobilitypricingservice.dto.TripPricedEvent;
import com.smart.mobility.smartmobilitypricingservice.repository.DiscountRuleRepository;
import com.smart.mobility.smartmobilitypricingservice.repository.PricingResultRepository;
import com.smart.mobility.smartmobilitypricingservice.proxy.UserServiceClient;
import com.smart.mobility.smartmobilitypricingservice.messaging.PricingEventPublisher;
import com.smart.mobility.smartmobilitypricingservice.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

        @Mock
        private DiscountRuleRepository discountRuleRepository;

        @Mock
        private PricingResultRepository pricingResultRepository;

        @Mock
        private UserServiceClient userServiceClient;

        @Mock
        private PricingEventPublisher pricingEventPublisher;

        @Spy
        private ObjectMapper objectMapper = new ObjectMapper();

        @InjectMocks
        private PricingService pricingService;

        @BeforeEach
        void setUp() {
                // Setup mock defaults
                lenient().when(pricingResultRepository.save(any(PricingResult.class))).thenAnswer(invocation -> {
                        PricingResult result = invocation.getArgument(0);
                        result.setId(1L);
                        return result;
                });
        }

        @Test
        void testProcessTripCompleted_BUS_NoDiscounts() {
                TripCompletedEvent event = TripCompletedEvent.builder()
                                .tripId(101L)
                                .userId(1L)
                                .transportType("BUS")
                                .numberOfSections(3) // 150 + 150 = 300
                                .build();

                when(userServiceClient.getActiveSubscription(1L)).thenReturn("NONE");
                when(discountRuleRepository.findByActiveTrueOrderByPriorityAsc()).thenReturn(List.of());

                pricingService.processTripCompleted(event);

                ArgumentCaptor<PricingResult> resultCaptor = ArgumentCaptor.forClass(PricingResult.class);
                verify(pricingResultRepository).save(resultCaptor.capture());
                PricingResult savedResult = resultCaptor.getValue();

                assertThat(savedResult.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(300));
                assertThat(savedResult.getDiscountApplied()).isEqualByComparingTo(BigDecimal.ZERO);
                assertThat(savedResult.getFinalAmount()).isEqualByComparingTo(BigDecimal.valueOf(300));

                ArgumentCaptor<TripPricedEvent> eventCaptor = ArgumentCaptor.forClass(TripPricedEvent.class);
                verify(pricingEventPublisher).publishTripPricedEvent(eventCaptor.capture());
                TripPricedEvent publishedEvent = eventCaptor.getValue();

                assertThat(publishedEvent.basePrice()).isEqualByComparingTo(BigDecimal.valueOf(300));
                assertThat(publishedEvent.finalAmount()).isEqualByComparingTo(BigDecimal.valueOf(300));
                assertThat(publishedEvent.appliedDiscounts()).isEmpty();
        }

        @Test
        void testProcessTripCompleted_BRT_PriorityDiscounts() {
                TripCompletedEvent event = TripCompletedEvent.builder()
                                .tripId(102L)
                                .userId(2L)
                                .transportType("BRT")
                                .startZone(1)
                                .endZone(2) // Different zones -> 500
                                .build();

                when(userServiceClient.getActiveSubscription(2L)).thenReturn("MONTHLY");

                // Priority 1: SUBSCRIPTION -30%
                DiscountRule rule1 = DiscountRule.builder()
                                .ruleType("SUBSCRIPTION")
                                .percentage(BigDecimal.valueOf(30))
                                .priority(1)
                                .condition("MONTHLY")
                                .active(true)
                                .build();

                // Priority 2: OFFPEAK -20%
                DiscountRule rule2 = DiscountRule.builder()
                                .ruleType("OFFPEAK")
                                .percentage(BigDecimal.valueOf(20))
                                .priority(2)
                                .condition("ALL")
                                .active(true)
                                .build();

                // Priority 3: LOYALTY -10%
                DiscountRule rule3 = DiscountRule.builder()
                                .ruleType("LOYALTY")
                                .percentage(BigDecimal.valueOf(10))
                                .priority(3)
                                .condition(null) // applies to all
                                .active(true)
                                .build();

                when(discountRuleRepository.findByActiveTrueOrderByPriorityAsc())
                                .thenReturn(Arrays.asList(rule1, rule2, rule3));

                pricingService.processTripCompleted(event);

                // Expected Calculations:
                // Base = 500
                // -30% (150) -> 350
                // -20% (70) -> 280
                // -10% (28) -> 252
                // Total discount: 248

                ArgumentCaptor<PricingResult> resultCaptor = ArgumentCaptor.forClass(PricingResult.class);
                verify(pricingResultRepository).save(resultCaptor.capture());
                PricingResult savedResult = resultCaptor.getValue();

                assertThat(savedResult.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(500));
                assertThat(savedResult.getDiscountApplied()).isEqualByComparingTo(BigDecimal.valueOf(248));
                assertThat(savedResult.getFinalAmount()).isEqualByComparingTo(BigDecimal.valueOf(252));

                ArgumentCaptor<TripPricedEvent> eventCaptor = ArgumentCaptor.forClass(TripPricedEvent.class);
                verify(pricingEventPublisher).publishTripPricedEvent(eventCaptor.capture());
                TripPricedEvent publishedEvent = eventCaptor.getValue();

                assertThat(publishedEvent.appliedDiscounts()).hasSize(3);
                assertThat(publishedEvent.appliedDiscounts().get(0).getAmountDeducted())
                                .isEqualByComparingTo(BigDecimal.valueOf(150));
                assertThat(publishedEvent.appliedDiscounts().get(1).getAmountDeducted())
                                .isEqualByComparingTo(BigDecimal.valueOf(70));
                assertThat(publishedEvent.appliedDiscounts().get(2).getAmountDeducted())
                                .isEqualByComparingTo(BigDecimal.valueOf(28));
        }
}
