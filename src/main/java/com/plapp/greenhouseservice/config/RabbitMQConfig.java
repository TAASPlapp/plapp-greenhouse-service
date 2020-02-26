package com.plapp.greenhouseservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@PropertySource("classpath:application.properties")
public class RabbitMQConfig {

    @Value("${mq.gardener.queue}")
    private String gardenerQueueName;

    @Value("${mq.gardener.exchange}")
    private String gardenerExchange;

    @Value("${mq.gardener.routingKey}")
    private String gardenerRoutingKey;

    @Value("${mq.notification.queue}")
    private String notificationQueue;

    @Value("${mq.notification.exchange}")
    private String notificationExchange;

    @Value("${mq.notification.routingKey}")
    private String notificationRoutingKey;
}
