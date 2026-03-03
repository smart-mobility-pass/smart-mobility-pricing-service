package com.smart.mobility.smartmobilitypricingservice.proxy;

import com.smart.mobility.smartmobilitypricingservice.dto.PricingContextDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-mobility-pass-service")
public interface UserServiceClient {

    @GetMapping("/internal/users/{keycloakId}/pricing-context")
    PricingContextDTO getPricingContext(@PathVariable("keycloakId") String keycloakId);
}
