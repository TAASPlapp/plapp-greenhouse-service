package com.plapp.greenhouseservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.entities.utils.ApiResponse;
import com.plapp.greenhouseservice.controllers.GreenhouseController;
import com.plapp.greenhouseservice.repositories.PlantRepository;
import com.plapp.greenhouseservice.services.PlantService;
import com.plapp.greenhouseservice.services.StoryboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GreenhouseController.class)
class GreenhouseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlantService plantService;

    @MockBean
    private StoryboardService storyboardService;

    @Test
    void getPlants_validInput() throws Exception {
        long userId = 10;

        mockMvc.perform((RequestBuilder) get("/greenhouse/{userId}/plants", userId))
            .andExpect(status().isOk());
    }

    @Test
    void getPlants_invalidInput() throws Exception {
        mockMvc.perform((RequestBuilder) get("/greenhouse/userId/plants"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPlants_nonExistingInput() throws Exception {
        long userId = -1;
        MvcResult result = mockMvc.perform((RequestBuilder) get("/greenhouse/{userId}/plants", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("[]");
    }

    @Test
    void getPlant_validInput() throws Exception {
        long plantId = 1234;
        mockMvc.perform(get("/greenhouse/plant/{plantId}", plantId))
                .andExpect(status().isOk());
    }

    @Test
    void getPlant_invalidInput() throws Exception {
        mockMvc.perform(get("/greenhouse/plant/plantId"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPlant_validInput() throws Exception {
        Plant plant = PlantServiceTest.getMockPlant();

        mockMvc.perform(post("/greenhouse/plant/add")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(plant)))
                .andExpect(status().isOk());
    }

    @Test
    void addPlant_invalidInput() throws Exception {
        mockMvc.perform(post("/greenhouse/plant/add")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPlant_validInput_mapsToBusinessLogic() throws Exception {
        Plant plant = PlantServiceTest.getMockPlant();
        mockMvc.perform(post("/greenhouse/plant/add")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(plant)))
                .andExpect(status().isOk());

        ArgumentCaptor<Plant> plantCaptor = ArgumentCaptor.forClass(Plant.class);
        verify(plantService, times(1)).addPlant(plantCaptor.capture());
        assertThat(plantCaptor.getValue().getName()).isEqualTo(plant.getName());
        assertThat(plantCaptor.getValue().getStatus()).isEqualTo(plant.getStatus());
        assertThat(plantCaptor.getValue().getOwner()).isEqualTo(plant.getOwner());
        // ...
    }

    @Test
    void addPlant_validInput_returnsApiResponse() throws Exception {
        Plant plant = PlantServiceTest.getMockPlant();
        MvcResult mvcResult = mockMvc.perform(post("/greenhouse/plant/add")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(plant)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(true, "Plant created successfully");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void removePlant_validInput() throws Exception {
        long plantId = 0;
        mockMvc.perform(get("/greenhouse/plant/{plantId}/remove", plantId))
                .andExpect(status().isOk());
    }

    @Test
    void removePlant_validInput_mapsToBusinessLogic() throws Exception {
        long plantId = ((Double)(Math.PI*100)).longValue(); //lol
        mockMvc.perform(get("/greenhouse/plant/{plantId}/remove", plantId))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(plantService, times(1)).removePlant(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(plantId);
    }

    @Test
    void removePlant_validInput_returnsApiResponse() throws Exception {
        long plantId = ((Double)(Math.PI*100)).longValue(); //lol
        MvcResult mvcResult = mockMvc.perform(get("/greenhouse/plant/{plantId}/remove", plantId))
                .andExpect(status().isOk())
                .andReturn();
        ApiResponse expectedResponse = new ApiResponse(true, "Plant removed");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void removePlant_invalidInput() throws Exception {
        mockMvc.perform(get("/greenhouse/plant/plantId/remove"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStoryboardByPlant_validInput() throws Exception {
        long plantId = 0;
        mockMvc.perform(get("/greenhouse/plant/{plantId}/storyboard", plantId))
                .andExpect(status().isOk());
    }

    @Test
    void getStoryboardByPlant_validInput_mapsToBusinessLogic() throws Exception {
        long plantId = ((Double)(Math.PI*100)).longValue(); //lol
        mockMvc.perform(get("/greenhouse/plant/{plantId}/storyboard", plantId))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(storyboardService, times(1)).getStoryboardByPlantId(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(plantId);
    }

    @Test
    void getStoryboards_validInput_returnsStoryboard() throws Exception {
        when(storyboardService.getStoryboardByPlantId(any(Long.class))).thenReturn(new Storyboard());

        long plantId = 0;
        MvcResult mvcResult = mockMvc.perform(get("/greenhouse/plant/{plantId}/storyboard", plantId))
                .andExpect(status().isOk())
                .andReturn();

        Storyboard expectedResponse = new Storyboard();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void getStoryboardByPlant_invalidInput() throws Exception {
        mockMvc.perform(get("/greenhouse/plant/plantId/storyboard"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStoryboards_validInput() throws Exception {
        mockMvc.perform(get("/greenhouse/storyboards"))
                .andExpect(status().isOk());
    }

    @Test
    void getStoryboards_validInput_mapsToBusinessLogic() throws Exception {

    }

    @Test
    void getStoryboards_validInput_returnsStoryboardList() throws Exception {

    }

    @Test
    void getStoryboards_invalidInput() throws Exception {

    }

    /* createStoryboard */
    @Test
    void createStoryboard_validInput() throws Exception {

    }

    @Test
    void createStoryboard_validInput_mapsToBusinessLogic() throws Exception {

    }

    @Test
    void createStoryboard_validInput_returnsApiResponse() throws Exception {

    }

    @Test
    void createStoryboard_invalidInput() throws Exception {

    }

    /* updateStoryboard */
    @Test
    void updateStoryboard_validInput() throws Exception {

    }

    @Test
    void updateStoryboard_validInput_mapsToBusinessLogic() throws Exception {

    }

    @Test
    void updateStoryboard_validInput_returnsApiResponse() throws Exception {

    }

    @Test
    void updateStoryboard_invalidInput() throws Exception {

    }

    /* removeStoryboard */
    @Test
    void removeStoryboard_validInput() throws Exception {

    }

    @Test
    void removeStoryboard_validInput_mapsToBusinessLogic() throws Exception {

    }

    @Test
    void removeStoryboard_validInput_returnsApiResponse() throws Exception {

    }

    @Test
    void removeStoryboard_invalidInput() throws Exception {

    }

    //TODO: decide whether to keep these controller methods

    /* addStoryboardItem */

    /* removeStoryboardItem */

}
