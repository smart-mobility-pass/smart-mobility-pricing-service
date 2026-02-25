package com.smart.mobility.smartmobilitypricingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "transport_type", length = 10, nullable = false)
    private String transportType;

    @Column(name = "base_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal basePrice;

    @Column(name = "discount_applied", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountApplied;

    @Column(name = "final_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal finalAmount;

    @Column(name = "applied_discounts", columnDefinition = "JSON")
    private String appliedDiscounts; // JSON string representing the list of applied discounts

    @CreationTimestamp
    @Column(name = "computed_at", updatable = false)
    private LocalDateTime computedAt;
}
