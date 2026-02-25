package com.smart.mobility.smartmobilitypricingservice.messaging;

import com.smart.mobility.smartmobilitypricingservice.config.RabbitMQConfig;
import com.smart.mobility.smartmobilitypricingservice.dto.TripPricedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PricingEventPublisherImpl implements PricingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishTripPricedEvent(TripPricedEvent event) {
        log.info("Publishing TripPricedEvent for tripId: {}", event.tripId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.TRIP_EXCHANGE, RabbitMQConfig.TRIP_PRICED_ROUTING_KEY, event);
    }
}
