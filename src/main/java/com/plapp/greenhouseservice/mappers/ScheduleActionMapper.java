package com.plapp.greenhouseservice.mappers;

import com.plapp.entities.messaging.ScheduleActionMQDTO;
import com.plapp.entities.schedules.ScheduleAction;
import com.plapp.greenhouseservice.services.PlantService;
import com.plapp.greenhouseservice.services.StoryboardService;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {PlantService.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ScheduleActionMapper {
    @Mapping(target = "plant", source = "plantId")
    ScheduleActionMQDTO scheduleActionToScheduleActionMQDTO(ScheduleAction scheduleAction);
}
