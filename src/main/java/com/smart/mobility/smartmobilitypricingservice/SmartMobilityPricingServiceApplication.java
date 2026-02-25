package com.smart.mobility.smartmobilitypricingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SmartMobilityPricingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartMobilityPricingServiceApplication.class, args);
    }

}
