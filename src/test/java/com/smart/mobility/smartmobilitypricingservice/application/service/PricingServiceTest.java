package com.smart.mobility.smartmobilitypricingservice.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.mobility.smartmobilitypricingservice.dto.UserMobilitySummaryDTO;
import com.smart.mobility.smartmobilitypricingservice.model.PricingResult;
import com.smart.mobility.smartmobilitypricingservice.dto.TripCompletedEvent;
import com.smart.mobility.smartmobilitypricingservice.dto.TripPricedEvent;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

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
                lenient().when(pricingResultRepository.save(any(PricingResult.class))).thenAnswer(invocation -> {
                        PricingResult result = invocation.getArgument(0);
                        result.setId(1L);
                        return result;
                });
        }

        @Test
        void testCalculateAndProcessTrip_BUS_NoDiscounts_UnderCap() {
                TripCompletedEvent event = TripCompletedEvent.builder()
                                .tripId(101L)
                                .userId("user-123")
                                .transportType("BUS")
                                .numberOfSections(3) // 150 + (3 * 50) = 300
                                .build();

                UserMobilitySummaryDTO summary = UserMobilitySummaryDTO.builder()
                                .keycloakId("user-123")
                                .hasActivePass(true)
                                .dailyCap(25.0)
                                .currentSpent(10.0)
                                .activeDiscountRate(0.0)
                                .build();

                when(userServiceClient.getUserSummary("user-123")).thenReturn(summary);

                pricingService.calculateAndProcessTrip(event);

                ArgumentCaptor<PricingResult> resultCaptor = ArgumentCaptor.forClass(PricingResult.class);
                verify(pricingResultRepository).save(resultCaptor.capture());
                PricingResult savedResult = resultCaptor.getValue();

                assertThat(savedResult.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(300));
                assertThat(savedResult.getFinalAmount()).isEqualByComparingTo(BigDecimal.valueOf(300));

                verify(pricingEventPublisher).publishTripPricedEvent(any(TripPricedEvent.class));
        }

        @Test
        void testCalculateAndProcessTrip_TER_WithDiscount_HittingCap() {
                TripCompletedEvent event = TripCompletedEvent.builder()
                                .tripId(102L)
                                .userId("user-456")
                                .transportType("TER")
                                .numberOfSections(2) // 500 + (2 * 500) = 1500
                                .build();

                UserMobilitySummaryDTO summary = UserMobilitySummaryDTO.builder()
                                .keycloakId("user-456")
                                .hasActivePass(true)
                                .dailyCap(25.0)
                                .currentSpent(20.0) // 5.0 remaining
                                .activeDiscountRate(0.2) // 20% discount
                                .build();

                when(userServiceClient.getUserSummary("user-456")).thenReturn(summary);

                // Base 1500
                // After 20% discount: 1500 - 300 = 1200
                // Hitting cap: remaining is 5.0 (assuming the unit is the same,
                // but let's assume Pricing uses small units or we need to align)
                // Actually the user examples use 2500 for 25.00€ if they use cents,
                // but my code uses summary.getDailyCap() which is Double.
                // If summary.getDailyCap() is 25.0, and event basePrice is 1500 (cents?), we
                // have a mismatch.
                // Let's assume everything is in the same unit. If base price is 1500, daily cap
                // should be 2500.

                summary.setDailyCap(2500.0);
                summary.setCurrentSpent(2000.0); // 500 remaining

                pricingService.calculateAndProcessTrip(event);

                ArgumentCaptor<PricingResult> resultCaptor = ArgumentCaptor.forClass(PricingResult.class);
                verify(pricingResultRepository).save(resultCaptor.capture());
                PricingResult savedResult = resultCaptor.getValue();

                assertThat(savedResult.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(1500));
                // Base 1500 - 20% = 1200. Remaining cap 500. Final should be 500.
                assertThat(savedResult.getFinalAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));
        }
}
