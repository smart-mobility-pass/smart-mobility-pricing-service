package com.smart.mobility.smartmobilitypricingservice.messaging;

import com.smart.mobility.smartmobilitypricingservice.service.PricingService;
import com.smart.mobility.smartmobilitypricingservice.config.RabbitMQConfig;
import com.smart.mobility.smartmobilitypricingservice.dto.TripCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TripEventListener {

    private final PricingService pricingService;

    @RabbitListener(queues = RabbitMQConfig.PRICING_QUEUE)
    public void handleTripCompletedEvent(TripCompletedEvent event) {
        log.info("Received TripCompletedEvent for tripId: {}", event.tripId());
        try {
            pricingService.calculateAndProcessTrip(event);
        } catch (Exception e) {
            log.error("Error processing TripCompletedEvent for tripId: {}", event.tripId(), e);
            // In a production environment, we would use a dead-letter queue or retry
            // mechanism here
            throw e;
        }
    }
}
