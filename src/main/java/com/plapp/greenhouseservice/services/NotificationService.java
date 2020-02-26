package com.plapp.greenhouseservice.services;

import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.greenhouseservice.entities.ScheduleActionMQDTO;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void notifyPendingScheduleAction(ScheduleActionMQDTO scheduleAction) throws ActorNotFoundException {
        if (scheduleAction.getPlant() == null)
            throw new ActorNotFoundException("Plant does not exist");

        // TODO: send notification to notification service MQ
    }
}
