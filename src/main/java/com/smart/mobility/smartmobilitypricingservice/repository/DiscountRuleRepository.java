package com.smart.mobility.smartmobilitypricingservice.repository;

import com.smart.mobility.smartmobilitypricingservice.model.DiscountRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRuleRepository extends JpaRepository<DiscountRule, Long> {
    List<DiscountRule> findByActiveTrueOrderByPriorityAsc();
}
