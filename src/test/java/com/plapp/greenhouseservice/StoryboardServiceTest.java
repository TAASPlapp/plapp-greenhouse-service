package com.plapp.greenhouseservice;
import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.entities.greenhouse.StoryboardItem;
import com.plapp.greenhouseservice.repositories.PlantRepository;
import com.plapp.greenhouseservice.repositories.StoryboardRepository;
import com.plapp.greenhouseservice.services.PlantService;
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


    private StoryboardService storyboardService;

    @BeforeEach
    void initTest() {
        storyboardService = new StoryboardService(storyboardRepository, plantRepository);
    }

    @Test
    void testCreateStoryboard() {
        Storyboard storyboard = new Storyboard();
        storyboard.setPlant(new Plant());
        storyboard.setSummary("summary");
        storyboard.setNumLikes(42);

        when(storyboardRepository.save(any(Storyboard.class))).then(returnsFirstArg());
        Storyboard savedStoryboard = storyboardService.createStoryboard(storyboard);

        assertThat(savedStoryboard.getSummary()).isEqualTo(storyboard.getSummary());
        assertThat(savedStoryboard.getNumLikes()).isEqualTo(storyboard.getNumLikes());
        // ...
    }

    @Test
    void testUpdate_NonExistingStoryboard() {
        assertThrows(ActorNotFoundException.class, () -> {
            storyboardService.removeStoryboard(-1);
        });
    }


    @Test
    void testUpdate_ExistingStoryboard() {
        Storyboard storyboard = new Storyboard();
        storyboard.setPlant(new Plant());
        storyboard.setSummary("summary");
        storyboard.setNumLikes(42);

        when(storyboardRepository.save(any(Storyboard.class))).then(returnsFirstArg());
        Storyboard savedStoryboard = storyboardService.createStoryboard(storyboard);
        when(storyboardRepository.existsById(savedStoryboard.getId())).thenReturn(true);
        assertNull(savedStoryboard.getStoryboardItems());

        List<StoryboardItem> items = new ArrayList<>();
        StoryboardItem item = new StoryboardItem(
                -1,
                "image",
                "thumb",
                "description",
                "title",
                Plant.PlantHealthStatus.HEALTHY,
                69
        );
        items.add(item);
        savedStoryboard.setStoryboardItems(items);

        assertDoesNotThrow(() -> {
            Storyboard updatedStoryboard = storyboardService.updateStoryboard(savedStoryboard);
            assertThat(updatedStoryboard.getStoryboardItems().get(0).getNumLikes()).isEqualTo(item.getNumLikes());
            assertThat(updatedStoryboard.getStoryboardItems().get(0).getDescription()).isEqualTo(item.getDescription());
            assertThat(updatedStoryboard.getStoryboardItems().get(0).getStatus()).isEqualTo(item.getStatus());
        });
    }
}
