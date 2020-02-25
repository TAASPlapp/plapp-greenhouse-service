package com.plapp.greenhouseservice.mappers;


import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.entities.greenhouse.StoryboardItem;
import com.plapp.greenhouseservice.entities.StoryboardDPO;
import com.plapp.greenhouseservice.entities.StoryboardItemDPO;
import com.plapp.greenhouseservice.services.StoryboardService;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {StoryboardService.class, StoryboardItemMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StoryboardMapper {
    StoryboardDPO storyboardToStoryboardDPO(Storyboard storyboard);
    List<StoryboardDPO> storyboardToStoryboardDPO(List<Storyboard> storyboard);

    Storyboard storyboardDPOToStoryboard(StoryboardDPO storyboardDPO);
    List<Storyboard> storyboardDPOToStoryboard(List<StoryboardDPO> storyboardDPO);
}
