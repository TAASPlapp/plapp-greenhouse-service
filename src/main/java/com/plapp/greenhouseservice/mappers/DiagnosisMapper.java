package com.plapp.greenhouseservice.mappers;

import com.plapp.entities.messaging.DiagnosisMQDTO;
import com.plapp.entities.schedules.Diagnosis;
import com.plapp.greenhouseservice.services.PlantService;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {PlantService.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DiagnosisMapper {
    @Mapping(target = "plant", source = "plantId")
    DiagnosisMQDTO diagnosisToDiagnosisMQDTO(Diagnosis diagnosis);
}