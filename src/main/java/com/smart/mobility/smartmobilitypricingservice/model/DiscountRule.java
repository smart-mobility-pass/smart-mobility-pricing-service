package com.smart.mobility.smartmobilitypricingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discount_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_type", length = 20, nullable = false)
    private String ruleType; // SUBSCRIPTION, OFFPEAK, LOYALTY, DAILY_CAP

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal percentage;

    @Column(nullable = false)
    private Integer priority; // 1 = highest

    @Column(columnDefinition = "TEXT")
    private String condition;

    @Column(nullable = false)
    private Boolean active;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
