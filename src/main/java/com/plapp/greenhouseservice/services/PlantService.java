package com.plapp.greenhouseservice.services;

import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.greenhouseservice.repositories.PlantRepository;
import com.plapp.greenhouseservice.repositories.StoryboardRepository;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantService {
    private final PlantRepository plantRepository;
    private final StoryboardRepository storyboardRepository;

    public PlantService(PlantRepository plantRepository,
                        StoryboardRepository storyboardRepository) {
        this.plantRepository = plantRepository;
        this.storyboardRepository = storyboardRepository;
    }

    public List<Plant> findByOwner(long userId) {
        return plantRepository.findByOwner(userId);
    }

    public Plant getPlant(long plantId) {
        return plantRepository.findById(plantId).orElse(null);
    }

    public Plant addPlant(Plant plant) throws HibernateException {
        return plantRepository.save(plant);
    }

    public void removePlant(long plantId) throws HibernateException,
                                                 ActorNotFoundException {
        Plant plant = plantRepository.findById(plantId).orElse(null);
        if (plant == null)
            throw new ActorNotFoundException("Plant does not exist");

        Storyboard storyboard = storyboardRepository.findByPlant(plant);
        if (storyboard != null)
            storyboardRepository.deleteById(storyboard.getId());

        plantRepository.delete(plant);
    }
}
