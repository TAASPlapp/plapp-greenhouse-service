package com.plapp.greenhouseservice.controllers;

import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.entities.greenhouse.StoryboardItem;
import com.plapp.entities.utils.ApiResponse;
import com.plapp.greenhouseservice.services.PlantService;
import com.plapp.greenhouseservice.services.StoryboardService;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/greenhouse")
public class GreenhouseController {

    private final PlantService plantService;
    private final StoryboardService storyboardService;

    @Autowired
    public GreenhouseController(PlantService plantService,
                                StoryboardService storyboardService) {
        this.plantService = plantService;
        this.storyboardService = storyboardService;
    }


    @GetMapping("/{userId}/plants")
    public List<Plant> getPlants(@PathVariable(value="userId") long userId) {
        return plantService.findByOwner(userId);
    }

    @GetMapping("/plant/{plantId}")
    public Plant getPlant(@PathVariable(value="plantId") long plantId) {
        return plantService.getPlant(plantId);
    }

    @PostMapping("/plant/add")
    public ApiResponse addPlant(@RequestBody Plant plant) {
        try {
            plantService.addPlant(plant);
        } catch (HibernateException e) {
            return new ApiResponse(false, "Could not create entity: " + e.getMessage());
        }

        return new ApiResponse(true, "Plant created successfully");
    }

    @GetMapping("/plant/{plantId}/remove")
    public ApiResponse removePlant(@PathVariable(value="plantId") long plantId) {
        try {
            plantService.removePlant(plantId);
        } catch (ActorNotFoundException e) {
            return new ApiResponse(false, e.getMessage());
        } catch (HibernateException e) {
            return new ApiResponse(false, "Could not remove plant: " + e.getMessage());
        }

        return new ApiResponse(true, "Plant removed");
    }

    @GetMapping("/plant/{plantId}/storyboard")
    public Storyboard getStoryboard(@PathVariable(value="plantId") long plantId) {
       return storyboardService.getStoryboardByPlantId(plantId);

    }

    @GetMapping("/storyboards")
    public List<Storyboard> getStoryboards() {
        return storyboardService.getAllStoryboards();
    }

    @PostMapping("/storyboard/create")
    public ApiResponse createStoryboard(@RequestBody Storyboard storyboard) {
        try {
            storyboardService.createStoryboard(storyboard);
        } catch (HibernateException e) {
            return new ApiResponse(false, "Could not create storyboard: " + e.getMessage());
        }

        return new ApiResponse();
    }

    @PostMapping("/storyboard/update")
    public ApiResponse updateStoryboard(@RequestBody Storyboard storyboard) {
        try {
            storyboardService.updateStoryboard(storyboard);
        } catch (ActorNotFoundException e) {
            return new ApiResponse(false, e.getMessage());
        } catch (HibernateException e) {
            return new ApiResponse(false, "Could not update storyboard: " + e.getMessage());
        }
        return new ApiResponse();
    }

    @GetMapping("/storyboard/{storyboardId}/remove")
    @Transactional
    public ApiResponse removeStoryboard(@PathVariable(value="storyboardId") long storyboardId) {
        try {
            storyboardService.removeStoryboard(storyboardId);
        } catch (ActorNotFoundException e) {
            return new ApiResponse(false, e.getMessage());
        } catch (HibernateException e) {
            return new ApiResponse(false, "Could not delete storyboard: " + e.getMessage());
        }
        return new ApiResponse();
    }

    @PostMapping("/storyboard/{storyboardId}/item/add")
    public ApiResponse addStoryboardItem(@PathVariable(value="storyboardId") long storyboardId,
                                         @RequestBody StoryboardItem storyboardItem) {
        try {
            storyboardService.addStoryboardItem(storyboardId, storyboardItem);
        } catch (ActorNotFoundException e) {
            return new ApiResponse(false, e.getMessage());
        } catch (HibernateException e) {
            return new ApiResponse(false, "Could not add item: " + e.getMessage());
        }
        return new ApiResponse();
    }

    @GetMapping("/storyboard/{storyboardId}/item/{itemId}/remove")
    public ApiResponse removeStoryboardItem(@PathVariable(value="storyboardId") long storyboardId,
                                            @PathVariable(value="itemId") long itemId) {
        try {
            storyboardService.removeStoryboardItem(storyboardId, itemId);
        } catch (ActorNotFoundException | IllegalArgumentException e) {
            return new ApiResponse(false, e.getMessage());
        } catch (HibernateException e) {
            return new ApiResponse(false, "Could not remove item:" + e.getMessage());
        }
        return new ApiResponse();
    }
}
