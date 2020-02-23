package com.plapp.greenhouseservice.controllers;

import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.entities.greenhouse.StoryboardItem;
import com.plapp.entities.utils.ApiResponse;
import com.plapp.greenhouseservice.repositories.PlantRepository;
import com.plapp.greenhouseservice.repositories.StoryboardItemRepository;
import com.plapp.greenhouseservice.repositories.StoryboardRepository;
import org.lists.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/greenhouse")
public class GreenhouseController {

    private PlantRepository plantRepository;
    private StoryboardItemRepository storyboardItemRepository;
    private StoryboardRepository storyboardRepository;

    @Autowired
    public GreenhouseController(PlantRepository plantRepository,
                                StoryboardItemRepository storyboardItemRepository,
                                StoryboardRepository storyboardRepository) {
        this.plantRepository = plantRepository;
        this.storyboardItemRepository = storyboardItemRepository;
        this.storyboardRepository = storyboardRepository;
    }


    @GetMapping("/plants")
    public List<Plant> getPlants(@RequestParam long userId) {
        return plantRepository.findByOwner(userId);
    }

    @GetMapping("/plant/{plantId}")
    public Plant getPlant(@PathVariable(value="plantId") long plantId) {
        Optional<Plant> plant = plantRepository.findById(plantId);
        return plant.orElse(null);
    }

    @PostMapping("/plant/add")
    public ApiResponse addPlant(@RequestBody Plant plant) {
        plantRepository.save(plant);
        return new ApiResponse();
    }

    @GetMapping("/plant/{plantId}/remove")
    public ApiResponse removePlant(@PathVariable(value="plantId") long plantId) {
        Optional<Plant> plant = plantRepository.findById(plantId);

        if (!plant.isPresent())
            return new ApiResponse(false, "Plant does not exist");

        Storyboard storyboard = storyboardRepository.findByPlant(plant.get());
        if (storyboard != null) {
            storyboardRepository.delete(storyboard);
        }

        plantRepository.delete(plant.get());

        return new ApiResponse();
    }

    @GetMapping("/plant/{plantId}/storyboard")
    public Storyboard getStoryboard(@PathVariable(value="plantId") long plantId) {
        Optional<Plant> plant = plantRepository.findById(plantId);
        return plant.map(value -> storyboardRepository.findByPlant(value)).orElse(null);

    }

    @GetMapping("/storyboards")
    public List<Storyboard> getStoryboards() {
        return storyboardRepository.findAll();
    }

    @PostMapping("/storyboard/create")
    public ApiResponse createStoryboard(@RequestBody Storyboard storyboard) {
        storyboard.setId(-1);
        if (storyboardRepository.save(storyboard).getId() == -1)
            return new ApiResponse(false, "Could not create storyboard");
        return new ApiResponse();
    }

    @PostMapping("/storyboard/update")
    public ApiResponse updateStoryboard(@RequestBody Storyboard storyboard) {
        if (!storyboardRepository.existsById(storyboard.getId()))
            return new ApiResponse(false, "Storyboard does not exist");

        return this.createStoryboard(storyboard);
    }

    @GetMapping("/storyboard/{storyboardId}/remove")
    @Transactional
    public ApiResponse removeStoryboard(@PathVariable(value="storyboardId") long storyboardId) {
        if (!storyboardRepository.existsById(storyboardId))
            return new ApiResponse(false, "Storyboard does not exist");
        storyboardRepository.deleteById(storyboardId);
        return new ApiResponse();
    }

    @PostMapping("/storyboard/{storyboardId}/item/add")
    public ApiResponse addStoryboardItem(@PathVariable(value="storyboardId") long storyboardId,
                                         @RequestBody StoryboardItem storyboardItem) {
        Optional<Storyboard> optStoryboard = storyboardRepository.findById(storyboardId);
        if (!optStoryboard.isPresent())
            return new ApiResponse(false, "Storyboard does not exist");

        Storyboard storyboard = optStoryboard.get();

        List<StoryboardItem> items = storyboard.getStoryboardItems();
        if (items == null || items.size() < 1)
            items = new ArrayList<>();
        items.add(storyboardItem);

        storyboard.setStoryboardItems(items);

        return this.createStoryboard(storyboard);
    }

    @GetMapping("/storyboard/{storyboardId}/item/{itemId}/remove")
    public ApiResponse removeStoryboardItem(@PathVariable(value="storyboardId") long storyboardId,
                                            @PathVariable(value="itemId") long itemId) {
        Optional<Storyboard> optStoryboard = storyboardRepository.findById(storyboardId);
        if (!optStoryboard.isPresent())
            return new ApiResponse(false, "Storyboard does not exist");

        Storyboard storyboard = optStoryboard.get();

        List<StoryboardItem> items = storyboard.getStoryboardItems();
        if (items == null || items.size() < 1)
            return new ApiResponse(false, "Storyboard does not have any item");

        List<StoryboardItem> matching = Lists.filter(items, i -> i.getId() == itemId);
        if (matching.size() < 1)
            return new ApiResponse(false, "Storyboard does not contain specified item");

        items.remove(matching.get(0));
        storyboard.setStoryboardItems(items);

        return this.createStoryboard(storyboard);
    }
}
