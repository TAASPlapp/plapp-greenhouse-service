package com.plapp.greenhouseservice.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.entities.greenhouse.StoryboardItem;
import com.plapp.entities.messaging.DiagnosisMQDTO;
import com.plapp.entities.messaging.ScheduleActionMQDTO;
import com.plapp.entities.schedules.Diagnosis;
import com.plapp.entities.schedules.ScheduleAction;
import com.plapp.greenhouseservice.config.RabbitMQConfig;
import com.plapp.greenhouseservice.entities.StoryboardDPO;
import com.plapp.greenhouseservice.entities.StoryboardItemDPO;
import com.plapp.greenhouseservice.mappers.DiagnosisMapper;
import com.plapp.greenhouseservice.mappers.ScheduleActionMapper;
import com.plapp.greenhouseservice.repositories.StoryboardItemRepository;
import com.plapp.greenhouseservice.repositories.StoryboardRepository;
import com.plapp.greenhouseservice.services.NotificationService;
import com.plapp.greenhouseservice.services.PlantService;
import com.plapp.greenhouseservice.services.StoryboardItemService;
import com.plapp.greenhouseservice.services.StoryboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GardenerMQListener {
    private final RabbitMQConfig rabbitMQConfig;

    private final NotificationService notificationService;

    private final ScheduleActionMapper scheduleActionMapper;
    private final DiagnosisMapper diagnosisMapper;

    private final PlantService plantService;
    private final StoryboardRepository storyboardRepository;
    private final StoryboardItemRepository storyboardItemRepository;

    @Bean
    public Queue gardenerQueue() {
        return new Queue(rabbitMQConfig.getGardenerQueueName(), false);
    }

    @Bean
    TopicExchange gardenerExchange() {
        return new TopicExchange(rabbitMQConfig.getGardenerExchange());
    }

    @Bean
    Binding gardenerBinding(@Qualifier("gardenerQueue") Queue queue, @Qualifier("gardenerExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(rabbitMQConfig.getGardenerRoutingKey());
    }


    @Transactional
    @RabbitListener(queues = "${mq.gardener.queue}")
    public void receiveMessage(final Message message) throws JsonProcessingException  {
        String body = new String(message.getBody());

        if (message.getMessageProperties().getHeaders().containsValue("com.plapp.entities.schedules.ScheduleAction")) {
            ScheduleAction scheduleAction = new ObjectMapper().readValue(body, ScheduleAction.class);

            System.out.println(String.format("Received ScheduleAction from %s: %s", rabbitMQConfig.getGardenerQueueName(), scheduleAction));

            try {
                ScheduleActionMQDTO scheduleActionMQDTO = scheduleActionMapper.scheduleActionToScheduleActionMQDTO(scheduleAction);
                notificationService.notifyPendingScheduleAction(scheduleActionMQDTO);
            } catch (Exception e) {
                throw new AmqpRejectAndDontRequeueException(e);
            }

        } else if (message.getMessageProperties().getHeaders().containsValue("com.plapp.entities.schedules.Diagnosis")) {
            Diagnosis diagnosis = new ObjectMapper().readValue(body, Diagnosis.class);
            System.out.println(String.format("Received diagnosis from %s: %s", rabbitMQConfig.getGardenerQueueName(), diagnosis));

            System.out.println("Updating plant status");
            diagnosis.isIll();
            Plant plant = plantService.getPlant(Integer.parseInt(diagnosis.getPlantId()));
            StoryboardDPO storyboard = storyboardRepository.findByPlant(plant);
            List<StoryboardItemDPO> storyboardItemDPOList = storyboard.getStoryboardItems();
            StoryboardItemDPO storyboardItemDPO = storyboardItemDPOList.get(storyboardItemDPOList.size() - 1);

            if (diagnosis.isIll()) {
                System.out.println("Plant is sick");
                plant.setStatus(Plant.PlantHealthStatus.SICK);
                storyboardItemDPO.setStatus(Plant.PlantHealthStatus.SICK);

            } else {
                System.out.println("Plant is healthy");
                plant.setStatus(Plant.PlantHealthStatus.HEALTHY);
                storyboardItemDPO.setStatus(Plant.PlantHealthStatus.HEALTHY);
            }

            plantService.addPlant(plant);
            storyboardItemRepository.save(storyboardItemDPO);


            try {
                DiagnosisMQDTO diagnosisMQDTO = diagnosisMapper.diagnosisToDiagnosisMQDTO(diagnosis);
                notificationService.sendDiagnosis(diagnosisMQDTO);
            } catch (Exception e) {
                throw new AmqpRejectAndDontRequeueException(e);
            }
        }
    }

}
