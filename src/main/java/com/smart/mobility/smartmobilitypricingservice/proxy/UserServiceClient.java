package com.smart.mobility.smartmobilitypricingservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.url:http://localhost:8081}")
public interface UserServiceClient {

    @GetMapping("/internal/subscriptions/{userId}")
    String getActiveSubscription(@PathVariable("userId") Long userId);
}
