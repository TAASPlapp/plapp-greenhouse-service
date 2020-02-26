package com.plapp.greenhouseservice.entities;

import com.plapp.entities.greenhouse.Plant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class ScheduleActionMQDTO {
    private long userId;
    private Plant plant;
    private String action;
    private Date date;
    private int periodicity;
    private String additionalInfo;
}
