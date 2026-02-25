package com.smart.mobility.smartmobilitypricingservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
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

    // ─── Exchange Bean ───────────────────────────────────────────────────────
    @Bean
    public DirectExchange tripExchange() {
        return new DirectExchange(TRIP_EXCHANGE);
    }

    // ─── Queue Bean ──────────────────────────────────────────────────────────
    @Bean
    public Queue pricingQueue() {
        return new Queue(PRICING_QUEUE, true);
    }

    @Bean
    public Binding bindingPricingQueue(Queue pricingQueue, DirectExchange tripExchange) {
        return BindingBuilder.bind(pricingQueue).to(tripExchange).with(TRIP_COMPLETED_ROUTING_KEY);
    }

    // ─── JSON Message Converter ───────────────────────────────────────────────
    @Bean
    public MessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        // Ignore __TypeId__ header, infer type from method signature logic
        converter.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}
