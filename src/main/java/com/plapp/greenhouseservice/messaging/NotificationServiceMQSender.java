package com.plapp.greenhouseservice.messaging;

import com.plapp.entities.messaging.DiagnosisMQDTO;
import com.plapp.entities.messaging.ScheduleActionMQDTO;
import com.plapp.greenhouseservice.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceMQSender {
    private final RabbitMQConfig rabbitMQConfig;
    private final RabbitTemplate rabbitTemplate;

    @Bean
    public Queue notificationQueue() {
        return new Queue(rabbitMQConfig.getNotificationQueue(), false);
    }

    @Bean
    TopicExchange notificationExchange() {
        return new TopicExchange(rabbitMQConfig.getNotificationExchange());
    }

    @Bean
    Binding notificationBinding(@Qualifier("notificationQueue") Queue queue, @Qualifier("notificationExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(rabbitMQConfig.getNotificationRoutingKey());
    }

    public void sendScheduleAction(ScheduleActionMQDTO scheduleActionMQDTO) {
        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getNotificationExchange(),
                rabbitMQConfig.getNotificationRoutingKey(),
                scheduleActionMQDTO
        );
    }

    public void sendDiagnosis(DiagnosisMQDTO diagnosisMQDTO) {
        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getNotificationExchange(),
                rabbitMQConfig.getNotificationRoutingKey(),
                diagnosisMQDTO
        );
    }
}
