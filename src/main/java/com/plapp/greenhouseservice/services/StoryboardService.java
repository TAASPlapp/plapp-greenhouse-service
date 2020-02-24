package com.plapp.greenhouseservice.services;

import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.entities.greenhouse.StoryboardItem;
import com.plapp.greenhouseservice.repositories.PlantRepository;
import com.plapp.greenhouseservice.repositories.StoryboardRepository;
import org.hibernate.HibernateException;
import org.lists.utils.Lists;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StoryboardService {
    private final StoryboardRepository storyboardRepository;
    private final PlantRepository plantRepository;

    public StoryboardService(StoryboardRepository storyboardRepository,
                             PlantRepository plantRepository) {
        this.storyboardRepository = storyboardRepository;
        this.plantRepository = plantRepository;
    }

    public Storyboard getStoryboardByPlantId(long plantId) {
        Plant plant = plantRepository.findById(plantId).orElse(null);
        return storyboardRepository.findByPlant(plant);
    }

    public List<Storyboard> getAllStoryboards() {
        return storyboardRepository.findAll();
    }

    public Storyboard createStoryboard(Storyboard storyboard) throws HibernateException {
        return storyboardRepository.save(storyboard);
    }

    public Storyboard updateStoryboard(Storyboard storyboard) throws HibernateException,
                                                                     ActorNotFoundException {
        if (!storyboardRepository.existsById(storyboard.getId()))
            throw new ActorNotFoundException("Storyboard does not exists");

        return this.createStoryboard(storyboard);
    }

    public void removeStoryboard(long storyboardId) throws HibernateException,
                                                           ActorNotFoundException {
        if (!storyboardRepository.existsById(storyboardId))
            throw new ActorNotFoundException("Storyboard does not exists");
        storyboardRepository.deleteById(storyboardId);
    }

    public Storyboard addStoryboardItem(long storyboardId, StoryboardItem item) throws HibernateException,
                                                                                         ActorNotFoundException {
        Storyboard storyboard = storyboardRepository.findById(storyboardId).orElse(null);
        if (storyboard == null)
            throw new ActorNotFoundException("Storyboard does not exsist");

        List<StoryboardItem> items = storyboard.getStoryboardItems();
        if (items == null || items.size() < 1)
            items = new ArrayList<>();
        items.add(item);

        return this.createStoryboard(storyboard);
    }

    public Storyboard removeStoryboardItem(long storyboardId, long itemId) throws HibernateException,
                                                                            ActorNotFoundException,
                                                                            IllegalArgumentException {
        Storyboard storyboard = storyboardRepository.findById(storyboardId).orElse(null);
        if (storyboard == null)
            throw new ActorNotFoundException("Storyboard does not exist");

        List<StoryboardItem> items = storyboard.getStoryboardItems();
        if (items == null || items.size() < 1)
            throw new IllegalArgumentException("Storyboard does not have any item");

        List<StoryboardItem> matching = Lists.filter(items, i -> i.getId() == itemId);
        if (matching.size() < 1)
            throw new ActorNotFoundException("Storyboard does not contain the specified item");

        items.remove(matching.get(0));
        storyboard.setStoryboardItems(items);

        return this.createStoryboard(storyboard);
    }
}
