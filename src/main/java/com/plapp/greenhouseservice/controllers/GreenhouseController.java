package com.plapp.greenhouseservice.controllers;

import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.entities.greenhouse.StoryboardItem;
import com.plapp.entities.utils.ApiResponse;
import com.plapp.greenhouseservice.repositories.PlantRepository;
import com.plapp.greenhouseservice.repositories.StoryboardItemRepository;
import com.plapp.greenhouseservice.repositories.StoryboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
        return null;
    }

    @GetMapping("/plant")
    public Plant getPlant(@RequestParam long plantId) {
        return null;
    }

    @PostMapping("/plant/add")
    public ApiResponse addPlant(@RequestBody Plant plant) {
        plantRepository.save(plant);
        return new ApiResponse();
    }

    @PostMapping("/plant/remove")
    public ApiResponse removePlant(@RequestBody Plant plant) {
        return null;
    }

    @GetMapping("/storyboards")
    public List<Storyboard> getStoryboards() {
        return storyboardRepository.findAll();
    }

    @GetMapping("/storyboard")
    public Storyboard getStoryboard(@RequestParam long plantId) {
        return null;
    }

    @PostMapping("/storyboard/create")
    public ApiResponse createStoryboard() {
        Storyboard storyboard = new Storyboard();
        storyboard.setPlant(plantRepository.findAll().get(0));
        storyboard.setSummary("This is a summary");
        storyboard.setNumLikes(69);

        StoryboardItem item = new StoryboardItem();
        item.setTitle("First item");
        item.setDescription("Description of first item");
        item.setStatus(Plant.PlantHealthStatus.HEALTHY);

        StoryboardItem item2 = new StoryboardItem();
        item2.setTitle("Second item");
        item2.setDescription("Description of second item");
        item2.setStatus(Plant.PlantHealthStatus.SICK);

        List<StoryboardItem> items = new ArrayList<>();
        items.add(item);
        items.add(item2);
        storyboard.setStoryboardItems(items);
        storyboardRepository.save(storyboard);

        return new ApiResponse();
    }

    @PostMapping("/storyboard/update")
    public ApiResponse updateStoryboard(@RequestBody Storyboard storyboard) {
        return null;
    }

    @GetMapping("/storyboard/remove")
    public ApiResponse removeStoryboard(@RequestBody Storyboard storyboard) {
        return null;
    }
}
