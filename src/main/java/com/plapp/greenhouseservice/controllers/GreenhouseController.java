package com.plapp.greenhouseservice.controllers;

import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.entities.greenhouse.StoryboardItem;
import com.plapp.entities.utils.ApiResponse;
import com.plapp.greenhouseservice.repositories.PlantRepository;
import com.plapp.greenhouseservice.repositories.StoryboardItemRepository;
import com.plapp.greenhouseservice.repositories.StoryboardRepository;
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

    @GetMapping("/plant")
    public Plant getPlant(@RequestParam long plantId) {
        Optional<Plant> plant = plantRepository.findById(plantId);
        return plant.orElse(null);
    }

    @PostMapping("/plant/add")
    public ApiResponse addPlant(@RequestBody Plant plant) {
        plantRepository.save(plant);
        return new ApiResponse();
    }

    @GetMapping("/plant/remove")
    public ApiResponse removePlant(@RequestParam long plantId) {
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

    @GetMapping("/storyboards")
    public List<Storyboard> getStoryboards() {
        return storyboardRepository.findAll();
    }

    @GetMapping("/storyboard")
    public Storyboard getStoryboard(@RequestParam long plantId) {
        Optional<Plant> plant = plantRepository.findById(plantId);
        if (!plant.isPresent())
            return null;

        return storyboardRepository.findByPlant(plant.get());
    }

    @PostMapping("/storyboard/create")
    public ApiResponse createStoryboard(@RequestBody Storyboard storyboard) {
        if (storyboardRepository.save(storyboard) == null)
            return new ApiResponse(false, "Could not create storyboard");
        return new ApiResponse();
    }

    @PostMapping("/storyboard/update")
    public ApiResponse updateStoryboard(@RequestBody Storyboard storyboard) {
        if (!storyboardRepository.existsById(storyboard.getId()))
            return new ApiResponse(false, "Storyboard does not exist");

        return this.createStoryboard(storyboard);
    }

    @GetMapping("/storyboard/remove")
    @Transactional
    public ApiResponse removeStoryboard(@RequestParam long storyboardId) {
        if (!storyboardRepository.existsById(storyboardId))
            return new ApiResponse(false, "Storyboard does not exist");
        storyboardRepository.deleteById(storyboardId);
        return new ApiResponse();
    }
}
