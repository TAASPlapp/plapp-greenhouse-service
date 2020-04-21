package com.plapp.greenhouseservice.controllers;

import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.entities.greenhouse.StoryboardItem;
import com.plapp.entities.utils.ApiResponse;
import com.plapp.greenhouseservice.entities.StoryboardDPO;
import com.plapp.greenhouseservice.entities.StoryboardItemDPO;
import com.plapp.greenhouseservice.mappers.StoryboardItemMapper;
import com.plapp.greenhouseservice.mappers.StoryboardMapper;
import com.plapp.greenhouseservice.services.PlantService;
import com.plapp.greenhouseservice.services.StoryboardItemService;
import com.plapp.greenhouseservice.services.StoryboardService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/greenhouse")
@RequiredArgsConstructor
public class GreenhouseController {

    private final PlantService plantService;
    private final StoryboardService storyboardService;
    private final StoryboardItemService storyboardItemService;


    private final StoryboardItemMapper storyboardItemMapper;
    private final StoryboardMapper storyboardMapper;

    @ControllerAdvice
    public static class GreenhouseControllerAdvice {
        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler({ActorNotFoundException.class})
        public void handle(ActorNotFoundException e) {}

        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        @ExceptionHandler({HibernateException.class})
        public void handle() {}
    }

    @GetMapping("/{userId}/plants")
    public List<Plant> getPlants(@PathVariable long userId) {
        return plantService.findByOwner(userId);
    }

    @GetMapping("/plant/{plantId}")
    public Plant getPlant(@PathVariable long plantId) {
        return plantService.getPlant(plantId);
    }

    @PostMapping("/{userId}/plants/add")
    public Plant addPlant(@PathVariable long userId,
                          @RequestBody Plant plant) {
        plant.setOwner(userId);
        Plant addedPlant = plantService.addPlant(plant);

        Storyboard storyboard = new Storyboard();
        storyboard.setPlant(addedPlant);
        storyboardService.createStoryboard(storyboardMapper.storyboardToStoryboardDPO(storyboard));

        return addedPlant;
    }

    @GetMapping("/plant/{plantId}/remove")
    public void removePlant(@PathVariable long plantId) throws ActorNotFoundException {
        plantService.removePlant(plantId);
    }

    @GetMapping("/plant/{plantId}/storyboard")
    public Storyboard getStoryboard(@PathVariable long plantId) {
       return storyboardMapper.storyboardDPOToStoryboard(
               plantService.getStoryboardByPlantId(plantId)
       );
    }

    @GetMapping("/storyboards")
    public List<Storyboard> getStoryboards() {
        return storyboardMapper
                .storyboardDPOToStoryboard(storyboardService.getAllStoryboards());
    }

    @GetMapping("/{userId}/storyboards")
    public List<Storyboard> getStoryboardsByUser(@PathVariable long userId) {
        return storyboardMapper.storyboardDPOToStoryboard(storyboardService.getUserStoryboards(userId));
    }

    @PostMapping("/plant/{plantId}/storyboard/create")
    public Storyboard createStoryboard(@PathVariable long plantId,
                                       @RequestBody Storyboard storyboard) {
        storyboard.setPlant(plantService.getPlant(plantId));
        StoryboardDPO storyboardDPO = storyboardService.createStoryboard(
                storyboardMapper.storyboardToStoryboardDPO(storyboard)
        );
        return storyboardMapper.storyboardDPOToStoryboard(storyboardDPO);
    }

    @PostMapping("/storyboard/{storyboardId}/update")
    public Storyboard updateStoryboard(@PathVariable long storyboardId,
                                       @RequestBody Storyboard storyboard) throws ActorNotFoundException {
        storyboard.setId(storyboardId);
        StoryboardDPO storyboardDPO = storyboardService.updateStoryboard(
                storyboardMapper.storyboardToStoryboardDPO(storyboard)
        );
        return storyboardMapper.storyboardDPOToStoryboard(storyboardDPO);
    }

    @GetMapping("/storyboard/{storyboardId}/remove")
    @Transactional
    public void removeStoryboard(@PathVariable long storyboardId) throws ActorNotFoundException {
        storyboardService.removeStoryboard(storyboardId);
    }

    @PostMapping("/storyboard/{storyboardId}/item/add")
    public StoryboardItem addStoryboardItem(@PathVariable long storyboardId,
                                            @RequestBody StoryboardItem storyboardItem) throws ActorNotFoundException {
        storyboardItem.setStoryboardId(storyboardId);
        StoryboardItemDPO itemDPO = storyboardItemService.addStoryboardItem(
                storyboardItemMapper.storyboardItemToStoryboardItemDPO(storyboardItem)
        );
        return storyboardItemMapper.storyboardItemDPOToStoryboardItem(itemDPO);
    }

    @GetMapping("/storyboard/item/{itemId}/remove")
    public void removeStoryboardItem(@PathVariable(value="itemId") long itemId) throws ActorNotFoundException {
        storyboardItemService.removeStoryboardItemById(itemId);
    }
}
