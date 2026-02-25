package com.smart.mobility.smartmobilitypricingservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TRIP_EXCHANGE = "trip.exchange";
    public static final String PRICING_QUEUE = "pricing.trip.completed.queue";
    public static final String TRIP_COMPLETED_ROUTING_KEY = "trip.completed";
    public static final String TRIP_PRICED_ROUTING_KEY = "trip.priced";

    @Bean
    public DirectExchange tripExchange() {
        return new DirectExchange(TRIP_EXCHANGE);
    }

    @Bean
    public Queue pricingQueue() {
        return new Queue(PRICING_QUEUE, true);
    }

    @Bean
    public Binding bindingPricingQueue(Queue pricingQueue, DirectExchange tripExchange) {
        return BindingBuilder.bind(pricingQueue).to(tripExchange).with(TRIP_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
