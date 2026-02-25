package com.smart.mobility.smartmobilitypricingservice.messaging;

import com.smart.mobility.smartmobilitypricingservice.dto.TripPricedEvent;

public interface PricingEventPublisher {
    void publishTripPricedEvent(TripPricedEvent event);
}
