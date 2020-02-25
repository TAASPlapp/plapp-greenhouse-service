package com.plapp.greenhouseservice;

import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.entities.greenhouse.StoryboardItem;
import com.plapp.greenhouseservice.entities.StoryboardDPO;
import com.plapp.greenhouseservice.entities.StoryboardItemDPO;
import com.plapp.greenhouseservice.mappers.StoryboardItemMapper;
import com.plapp.greenhouseservice.mappers.StoryboardItemMapperImpl;
import com.plapp.greenhouseservice.mappers.StoryboardMapper;
import com.plapp.greenhouseservice.mappers.StoryboardMapperImpl;
import com.plapp.greenhouseservice.repositories.PlantRepository;
import com.plapp.greenhouseservice.repositories.StoryboardItemRepository;
import com.plapp.greenhouseservice.repositories.StoryboardRepository;
import com.plapp.greenhouseservice.services.StoryboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class MappersTest {
    @Mock
    private StoryboardItemRepository storyboardItemRepository;

    @Mock
    private StoryboardRepository storyboardRepository;

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private StoryboardService storyboardService;

    private StoryboardItemMapper storyboardItemMapper;

    private StoryboardMapper storyboardMapper;

    @BeforeEach
    void initTest() {
        storyboardService = new StoryboardService(storyboardRepository);
        storyboardItemMapper = new StoryboardItemMapperImpl(storyboardService);
        storyboardMapper = new StoryboardMapperImpl(storyboardService, storyboardItemMapper);
    }

    @Test
    void mapStoryboardItem_to_StoryboardItemDPO() {
        StoryboardItem item = new StoryboardItem();
        item.setDescription("description");
        item.setStatus(Plant.PlantHealthStatus.HEALTHY);
        item.setStoryboardId(1234);

        StoryboardDPO storyboardDPO = new StoryboardDPO();
        storyboardDPO.setId(1234);
        when(storyboardRepository.findById(any(Long.class))).thenReturn(Optional.of(storyboardDPO));

        StoryboardItemDPO dpo = storyboardItemMapper
                .storyboardItemToStoryboardItemDPO(item);

        assertThat(dpo.getDescription()).isEqualTo(item.getDescription());
        assertThat(dpo.getStatus()).isEqualTo(item.getStatus());
        assertThat(dpo.getStoryboard().getId()).isEqualTo(item.getStoryboardId());

    }

    @Test
    void mapStoryboard_to_StoryboardDPO() {
        Storyboard storyboard = new Storyboard();
        storyboard.setSummary("summary");

        StoryboardItem item = new StoryboardItem();
        item.setDescription("description");
        item.setStatus(Plant.PlantHealthStatus.HEALTHY);

        List<StoryboardItem> items = new ArrayList<>();
        items.add(item);
        storyboard.setStoryboardItems(items);


        StoryboardDPO storyboardDPO = storyboardMapper.storyboardToStoryboardDPO(storyboard);
        assertThat(storyboardDPO.getStoryboardItems()).isNotNull();
        assertThat(storyboardDPO.getStoryboardItems().size()).isEqualTo(items.size());
    }

    @Test
    void mapStoryboardDPO_to_Storyboard() {
        StoryboardDPO storyboardDPO = new StoryboardDPO();
        storyboardDPO.setId(0);
        storyboardDPO.setSummary("storyboard summary");

        StoryboardItemDPO storyboardItemDPO = new StoryboardItemDPO();
        storyboardItemDPO.setStoryboard(storyboardDPO);
        storyboardItemDPO.setTitle("item");

        List<StoryboardItemDPO> itemsDPO = new ArrayList<>();
        itemsDPO.add(storyboardItemDPO);

        storyboardDPO.setStoryboardItems(itemsDPO);

        Storyboard storyboard = storyboardMapper.storyboardDPOToStoryboard(storyboardDPO);
        assertThat(storyboard.getStoryboardItems()).isNotNull();
        assertThat(storyboard.getStoryboardItems().get(0).getTitle()).isEqualTo(storyboardItemDPO.getTitle());
    }
}
