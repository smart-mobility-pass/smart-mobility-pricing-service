package com.smart.mobility.smartmobilitypricingservice.proxy;

import com.smart.mobility.smartmobilitypricingservice.dto.UserMobilitySummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-mobility-pass-service")
public interface UserServiceClient {

    @GetMapping("/api/users/summary/{keycloakId}")
    UserMobilitySummaryDTO getUserSummary(@PathVariable("keycloakId") String keycloakId);
}
