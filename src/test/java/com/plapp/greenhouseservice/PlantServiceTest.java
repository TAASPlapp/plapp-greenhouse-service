package com.plapp.greenhouseservice;

import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.entities.greenhouse.Plant;
import com.plapp.greenhouseservice.repositories.PlantRepository;
import com.plapp.greenhouseservice.repositories.StoryboardRepository;
import com.plapp.greenhouseservice.services.PlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PlantServiceTest {

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private StoryboardRepository storyboardRepository;

    private PlantService plantService;

    public static Plant getMockPlant() {
        Plant plant = new Plant();
        plant.setId(0);
        plant.setName("test-name");
        plant.setImage("test-image");
        plant.setType("type");
        plant.setOwner(1234);
        plant.setDescription("description");
        plant.setStatus(Plant.PlantHealthStatus.HEALTHY);
        return plant;
    }

    @BeforeEach
    void initTest() {
        plantService = new PlantService(plantRepository, storyboardRepository);
    }

    @Test
    void testAddPlant() {
        Plant plant = getMockPlant();

        when(plantRepository.save(any(Plant.class))).then(returnsFirstArg());
        Plant savedPlant = plantService.addPlant(plant);
        assertThat(savedPlant.getName()).isEqualTo(plant.getName());
        assertThat(savedPlant.getDescription()).isEqualTo(plant.getDescription());
        assertThat(savedPlant.getType()).isEqualTo(plant.getType());
        assertThat(savedPlant.getImage()).isEqualTo(plant.getImage());
        assertThat(savedPlant.getOwner()).isEqualTo(plant.getOwner());
        assertThat(savedPlant.getStatus()).isEqualTo(plant.getStatus());
    }

    @Test
    void remove_NonExistingPlant() {
        assertThrows(ActorNotFoundException.class, () -> {
            plantService.removePlant(-1);
        });
    }
}
