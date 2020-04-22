package com.plapp.greenhouseservice.messaging;

import com.plapp.entities.messaging.DiagnosisMQDTO;
import com.plapp.entities.messaging.ScheduleActionMQDTO;
import com.plapp.greenhouseservice.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceMQSender {
    private final RabbitMQConfig rabbitMQConfig;
    private final RabbitTemplate rabbitTemplate;
    private final Logger logger = LoggerFactory.getLogger(NotificationServiceMQSender.class);

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
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        logger.info("Sending ScheduleAction to notification service: " + scheduleActionMQDTO);
        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getNotificationExchange(),
                rabbitMQConfig.getNotificationRoutingKey(),
                scheduleActionMQDTO
        );
        logger.info("Sent");
    }

    public void sendDiagnosis(DiagnosisMQDTO diagnosisMQDTO) {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        logger.info("Sending Diagnosis to notification service: " +diagnosisMQDTO);
        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getNotificationExchange(),
                rabbitMQConfig.getNotificationRoutingKey(),
                diagnosisMQDTO
        );
        logger.info("Sent");
    }
}
