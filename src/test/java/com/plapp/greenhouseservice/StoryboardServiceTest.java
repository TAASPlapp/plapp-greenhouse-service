package com.plapp.greenhouseservice;
import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.entities.greenhouse.Plant;
import com.plapp.greenhouseservice.entities.StoryboardDPO;
import com.plapp.greenhouseservice.entities.StoryboardItemDPO;
import com.plapp.greenhouseservice.repositories.PlantRepository;
import com.plapp.greenhouseservice.repositories.StoryboardRepository;
import com.plapp.greenhouseservice.services.StoryboardItemService;
import com.plapp.greenhouseservice.services.StoryboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoryboardServiceTest {
    @Mock
    private PlantRepository plantRepository;

    @Mock
    private StoryboardRepository storyboardRepository;

    @Mock
    private StoryboardItemService storyboardItemService;

    private StoryboardService storyboardService;

    @BeforeEach
    void initTest() {
        storyboardService = new StoryboardService(storyboardRepository);
    }

    @Test
    void testCreateStoryboard() {
        StoryboardDPO storyboard = new StoryboardDPO();
        storyboard.setPlant(new Plant());
        storyboard.setSummary("summary");

        when(storyboardRepository.save(any(StoryboardDPO.class))).then(returnsFirstArg());

        assertDoesNotThrow(() -> {
            StoryboardDPO savedStoryboard = storyboardService.createStoryboard(storyboard);
            assertThat(savedStoryboard.getSummary()).isEqualTo(storyboard.getSummary());
            // ...
        });
    }

    @Test
    void testUpdate_NonExistingStoryboard() {
        assertThrows(ActorNotFoundException.class, () -> {
            storyboardService.removeStoryboard(-1);
        });
    }


    @Test
    void testUpdate_ExistingStoryboard() {
        StoryboardDPO storyboard = new StoryboardDPO();
        storyboard.setPlant(new Plant());
        storyboard.setSummary("summary");

        when(storyboardRepository.save(any(StoryboardDPO.class))).then(returnsFirstArg());
        StoryboardDPO savedStoryboard = storyboardService.createStoryboard(storyboard);

        //when(storyboardRepository.existsById(savedStoryboard.getId())).thenReturn(true);
        assertNull(savedStoryboard.getStoryboardItems());

        List<StoryboardItemDPO> items = new ArrayList<>();
        StoryboardItemDPO item = new StoryboardItemDPO();
        item.setId(-1);
        item.setStoryboard(savedStoryboard);
        item.setImage("image");
        item.setThumbImage("thumb");
        item.setDescription("description");
        item.setTitle("title");
        item.setStatus(Plant.PlantHealthStatus.HEALTHY);

        items.add(item);
        savedStoryboard.setStoryboardItems(items);

        assertDoesNotThrow(() -> {
            StoryboardDPO updatedStoryboard = storyboardService.createStoryboard(savedStoryboard);
            assertThat(updatedStoryboard.getStoryboardItems().get(0).getDescription()).isEqualTo(item.getDescription());
            assertThat(updatedStoryboard.getStoryboardItems().get(0).getStatus()).isEqualTo(item.getStatus());
        });
    }
}
