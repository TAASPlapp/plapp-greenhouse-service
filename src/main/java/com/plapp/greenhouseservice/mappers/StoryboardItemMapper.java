package com.plapp.greenhouseservice.mappers;

import com.plapp.entities.greenhouse.StoryboardItem;
import com.plapp.greenhouseservice.entities.StoryboardDPO;
import com.plapp.greenhouseservice.entities.StoryboardItemDPO;
import com.plapp.greenhouseservice.repositories.StoryboardItemRepository;
import com.plapp.greenhouseservice.services.StoryboardItemService;
import com.plapp.greenhouseservice.services.StoryboardService;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {StoryboardService.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StoryboardItemMapper {
    @Mapping(source = "storyboardId", target = "storyboard")
    StoryboardItemDPO storyboardItemToStoryboardItemDPO(StoryboardItem storyboardItem);
    List<StoryboardItemDPO> storyboardItemToStoryboardIdemDPO(List<StoryboardItem> storyboardItem);

    @Mapping(source = "storyboard.id", target = "storyboardId")
    StoryboardItem storyboardItemDPOToStoryboardItem(StoryboardItemDPO storyboardItemDPO);
    List<StoryboardItem> storyboardItemDPOToStoryboardItem(List<StoryboardItemDPO> storyboardItemDPO);
}
