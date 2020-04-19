package com.plapp.greenhouseservice.services;

import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.entities.messaging.DiagnosisMQDTO;
import com.plapp.entities.messaging.ScheduleActionMQDTO;
import com.plapp.greenhouseservice.messaging.NotificationServiceMQSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationServiceMQSender notificationServiceMQSender;

    public void notifyPendingScheduleAction(ScheduleActionMQDTO scheduleAction) throws ActorNotFoundException {
        //if (scheduleAction.getPlant() == null)
        //    throw new ActorNotFoundException("Plant does not exist");

        // TODO: send notification to notification service MQ
        notificationServiceMQSender.sendScheduleAction(scheduleAction);
    }

    public void sendDiagnosis(DiagnosisMQDTO diagnosisMQDTO) {
        notificationServiceMQSender.sendDiagnosis(diagnosisMQDTO);
    }
}
