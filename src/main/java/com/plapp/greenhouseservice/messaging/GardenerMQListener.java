package com.plapp.greenhouseservice.messaging;

import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.schedules.ScheduleAction;
import com.plapp.greenhouseservice.config.RabbitMQConfig;
import com.plapp.greenhouseservice.entities.ScheduleActionMQDTO;
import com.plapp.greenhouseservice.mappers.ScheduleActionMapper;
import com.plapp.greenhouseservice.services.NotificationService;
import com.plapp.greenhouseservice.services.PlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GardenerMQListener {
    private final RabbitMQConfig rabbitMQConfig;

    private final PlantService plantService;
    private final NotificationService notificationService;

    private final ScheduleActionMapper scheduleActionMapper;

    @Bean
    public Queue gardenerQueue() {
        return new Queue(rabbitMQConfig.getGardenerQueueName(), false);
    }

    @Bean
    TopicExchange gardenerExchange() {
        return new TopicExchange(rabbitMQConfig.getGardenerExchange());
    }

    @Bean
    Binding gardenerBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(rabbitMQConfig.getGardenerRoutingKey());
    }

    @RabbitListener(queues = "${mq.gardener.queue}")
    public void receiveScheduleAction(final ScheduleAction scheduleAction) {
        System.out.println(String.format("Received from %s: %s", rabbitMQConfig.getGardenerQueueName(), scheduleAction));

        try {
            ScheduleActionMQDTO scheduleActionMQDTO = scheduleActionMapper.scheduleActionToScheduleActionMQDTO(scheduleAction);
            notificationService.notifyPendingScheduleAction(scheduleActionMQDTO);
        } catch (ActorNotFoundException e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
