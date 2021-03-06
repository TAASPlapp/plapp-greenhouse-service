package com.plapp.greenhouseservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import com.plapp.entities.greenhouse.StoryboardItem;
import com.plapp.entities.utils.ApiResponse;
import com.plapp.greenhouseservice.controllers.GreenhouseController;
import com.plapp.greenhouseservice.entities.StoryboardDPO;
import com.plapp.greenhouseservice.entities.StoryboardItemDPO;
import com.plapp.greenhouseservice.mappers.StoryboardItemMapper;
import com.plapp.greenhouseservice.mappers.StoryboardMapper;
import com.plapp.greenhouseservice.services.PlantService;
import com.plapp.greenhouseservice.services.StoryboardItemService;
import com.plapp.greenhouseservice.services.StoryboardService;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @MockBean
    private StoryboardItemService storyboardItemService;

    @Autowired
    private StoryboardMapper storyboardMapper;

    @Autowired
    private StoryboardItemMapper storyboardItemMapper;

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

    /*@Test
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
    }*/

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

    /*@Test
    void removePlant_validInput_returnsApiResponse() throws Exception {
        long plantId = ((Double)(Math.PI*100)).longValue(); //lol
        MvcResult mvcResult = mockMvc.perform(get("/greenhouse/plant/{plantId}/remove", plantId))
                .andExpect(status().isOk())
                .andReturn();
        ApiResponse expectedResponse = new ApiResponse(true, "Plant removed");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }*/
    @Test
    void removePlant_notFound_returnsApiResponse() throws Exception {
        doThrow(new ActorNotFoundException("not found")).when(plantService).removePlant(any(Long.class));

        long plantId = ((Double)(Math.PI*100)).longValue(); //lol
        MvcResult mvcResult = mockMvc.perform(get("/greenhouse/plant/{plantId}/remove", plantId))
                .andExpect(status().isOk())
                .andReturn();
        ApiResponse expectedResponse = new ApiResponse(false, "not found");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    /*@Test
    void removePlant_throwsException_returnsApiResponse() throws Exception {
        doThrow(new HibernateException("hibernate exception")).when(plantService).removePlant(any(Long.class));

        long plantId = ((Double)(Math.PI*100)).longValue(); //lol
        MvcResult mvcResult = mockMvc.perform(get("/greenhouse/plant/{plantId}/remove", plantId))
                .andExpect(status().isOk())
                .andReturn();
        ApiResponse expectedResponse = new ApiResponse(false, "Could not remove plant: hibernate exception");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }*/

    @Test
    void removePlant_invalidInput() throws Exception {
        mockMvc.perform(get("/greenhouse/plant/plantId/remove"))
                .andExpect(status().isBadRequest());
    }

    // getStoryboard(long plantId);

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
        verify(plantService, times(1)).getStoryboardByPlantId(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(plantId);
    }

    @Test
    void getStoryboardByPlant_validInput_returnsStoryboard() throws Exception {
        when(plantService.getStoryboardByPlantId(any(Long.class))).thenReturn(new StoryboardDPO());

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

    // getStoryboards()

    @Test
    void getStoryboards_validInput() throws Exception {
        mockMvc.perform(get("/greenhouse/storyboards"))
                .andExpect(status().isOk());
    }

    @Test
    void getStoryboards_validInput_mapsToBusinessLogic() throws Exception {
        // No param
    }

    @Test
    void getStoryboards_validInput_returnsStoryboardList() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/greenhouse/storyboards"))
                .andExpect(status().isOk())
                .andReturn();
        List<Storyboard> expectedResponse = new ArrayList<>();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void getStoryboards_invalidInput() throws Exception {
       // No param
    }

    // createStoryboard(Storyboard storyboard)
    @Test
    void createStoryboard_validInput() throws Exception {
        Storyboard storyboard = new Storyboard();

        mockMvc.perform(post("/greenhouse/storyboard/create")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(storyboard)))
                .andExpect(status().isOk());
    }

    @Test
    void createStoryboard_validInput_mapsToBusinessLogic() throws Exception {
        Storyboard storyboard = new Storyboard();
        storyboard.setId(1234321);
        storyboard.setSummary("summary");
        Plant plant = new Plant();
        plant.setName("plant");
        storyboard.setPlant(plant);



        mockMvc.perform(post("/greenhouse/storyboard/create")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(storyboard)))
                .andExpect(status().isOk());

        ArgumentCaptor<StoryboardDPO> argumentCaptor = ArgumentCaptor.forClass(StoryboardDPO.class);
        verify(storyboardService, times(1)).createStoryboard(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId()).isEqualTo(storyboard.getId());
        assertThat(argumentCaptor.getValue().getSummary()).isEqualTo(storyboard.getSummary());
        assertThat(argumentCaptor.getValue().getPlant()).isEqualTo(storyboard.getPlant());
    }

    /*@Test
    void createStoryboard_validInput_returnsApiResponse() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/greenhouse/storyboard/create")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new Storyboard())))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(true, "Storyboard created");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }*/

    /*@Test
    void createStoryboard_throwsException_returnsApiResponse() throws Exception {
        when(storyboardService.createStoryboard(any(StoryboardDPO.class))).thenThrow(new HibernateException("hibernate error"));

        MvcResult mvcResult = mockMvc.perform(post("/greenhouse/storyboard/create")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new Storyboard())))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(false, "Could not create storyboard: hibernate error");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }*/

    @Test
    void createStoryboard_invalidInput() throws Exception {
        mockMvc.perform(post("/greenhouse/storyboard/create")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    // updateStoryboard
    @Test
    void updateStoryboard_validInput() throws Exception {
        Storyboard storyboard = new Storyboard();

        mockMvc.perform(post("/greenhouse/storyboard/{storyboardId}/update", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(storyboard)))
                .andExpect(status().isOk());
    }

    @Test
    void updateStoryboard_validInput_mapsToBusinessLogic() throws Exception {
        Storyboard storyboard = new Storyboard();
        storyboard.setId(1234321);
        storyboard.setSummary("summary");
        Plant plant = new Plant();
        plant.setName("plant");
        storyboard.setPlant(plant);

        mockMvc.perform(post("/greenhouse/storyboard/{storyboardId}/update", storyboard.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(storyboard)))
                .andExpect(status().isOk());

        ArgumentCaptor<StoryboardDPO> argumentCaptor = ArgumentCaptor.forClass(StoryboardDPO.class);
        verify(storyboardService, times(1)).updateStoryboard(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId()).isEqualTo(storyboard.getId());
        assertThat(argumentCaptor.getValue().getSummary()).isEqualTo(storyboard.getSummary());
        assertThat(argumentCaptor.getValue().getPlant()).isEqualTo(storyboard.getPlant());
    }

    @Test
    void updateStoryboard_validInput_returnsApiResponse() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/greenhouse/storyboard/1/update")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new Storyboard())))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(true, "Storyboard updated");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void updateStoryboard_notFound_returnsApiResponse() throws Exception {
        when(storyboardService.updateStoryboard(any(StoryboardDPO.class))).thenThrow(new ActorNotFoundException("not found"));

        MvcResult mvcResult = mockMvc.perform(post("/greenhouse/storyboard/1/update")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new Storyboard())))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(false, "not found");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void updateStoryboard_throwsException_returnsApiResponse() throws Exception {
        when(storyboardService.updateStoryboard(any(StoryboardDPO.class))).thenThrow(new ActorNotFoundException("hibernate error"));

        MvcResult mvcResult = mockMvc.perform(post("/greenhouse/storyboard/1/update")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new Storyboard())))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(false, "hibernate error");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void updateStoryboard_invalidInput() throws Exception {
        mockMvc.perform(post("/greenhouse/storyboard/1/update")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    // removeStoryboard
    @Test
    void removeStoryboard_validInput() throws Exception {
        mockMvc.perform(get("/greenhouse/storyboard/1/remove"))
                .andExpect(status().isOk());
    }

    @Test
    void removeStoryboard_validInput_mapsToBusinessLogic() throws Exception {
        long storyboardId = ((Double) (Math.PI * 100)).longValue();
        mockMvc.perform(get("/greenhouse/storyboard/{storyboardId}/remove", storyboardId))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(storyboardService, times(1)).removeStoryboard(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(storyboardId);
    }

    @Test
    void removeStoryboard_validInput_returnsApiResponse() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/greenhouse/storyboard/1/remove"))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(true, "Storyboard removed");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void removeStoryboard_notFound_returnsApiResponse() throws Exception {
        doThrow(new ActorNotFoundException("not found")).when(storyboardService).removeStoryboard(any(Long.class));

        MvcResult mvcResult = mockMvc.perform(get("/greenhouse/storyboard/1/remove"))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(false, "not found");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void removeStoryboard_throwsException_returnsApiResponse() throws Exception {
        doThrow(new HibernateException("hibernate exception")).when(storyboardService).removeStoryboard(any(Long.class));

        MvcResult mvcResult = mockMvc.perform(get("/greenhouse/storyboard/1/remove"))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(false, "Could not delete storyboard: hibernate exception");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void removeStoryboard_invalidInput() throws Exception {
         mockMvc.perform(get("/greenhouse/storyboard/notanumber/remove"))
                .andExpect(status().isBadRequest());
    }

    // addStoryboardItem(StoryboardItem)

    @Test
    void addStoryboardItem_validInput() throws Exception {
        StoryboardItem item = new StoryboardItem();
        mockMvc.perform(post("/greenhouse/storyboard/1/item/add")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk());
    }

    @Test
    void addStoryboardItem_validInput_mapsToBusinessLogic() throws Exception {
        long storyboardId = 1234;
        StoryboardItem storyboardItem = new StoryboardItem();
        storyboardItem.setTitle("title");
        storyboardItem.setStatus(Plant.PlantHealthStatus.HEALTHY);
        storyboardItem.setDescription("description");
        storyboardItem.setImage("image");
        storyboardItem.setThumbImage("thumb");

        StoryboardDPO storyboardDPO = new StoryboardDPO();
        storyboardDPO.setId(storyboardId);

        when(storyboardService.findById(any(Long.class))).thenReturn(storyboardDPO);

        mockMvc.perform(post("/greenhouse/storyboard/{storyboardId}/item/add", storyboardId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(storyboardItem)))
                .andExpect(status().isOk());

        ArgumentCaptor<StoryboardItemDPO> argumentCaptor = ArgumentCaptor.forClass(StoryboardItemDPO.class);
        verify(storyboardItemService, times(1)).addStoryboardItem(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getStoryboard().getId()).isEqualTo(storyboardId);
        assertThat(argumentCaptor.getValue().getTitle()).isEqualTo(storyboardItem.getTitle());
        assertThat(argumentCaptor.getValue().getStatus()).isEqualTo(storyboardItem.getStatus());
        assertThat(argumentCaptor.getValue().getDescription()).isEqualTo(storyboardItem.getDescription());
        assertThat(argumentCaptor.getValue().getImage()).isEqualTo(storyboardItem.getImage());
        assertThat(argumentCaptor.getValue().getThumbImage()).isEqualTo(storyboardItem.getThumbImage());
    }

    @Test
    void addStoryboardItem_validInput_returnsApiResponse() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/greenhouse/storyboard/1/item/add")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new StoryboardItem())))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(true, "Item added");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void addStoryboardItem_notFound_returnsApiResponse() throws Exception {
        when(storyboardItemService.addStoryboardItem(any(StoryboardItemDPO.class))).thenThrow(new ActorNotFoundException("not found"));

        MvcResult mvcResult = mockMvc.perform(post("/greenhouse/storyboard/1/item/add")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new StoryboardItem())))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(false, "not found");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);

    }

    @Test
    void addStoryboardItem_throwsException_returnsApiResponse() throws Exception {
        when(storyboardItemService.addStoryboardItem(any(StoryboardItemDPO.class))).thenThrow(new HibernateException("hibernate error"));

        MvcResult mvcResult = mockMvc.perform(post("/greenhouse/storyboard/1/item/add")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new StoryboardItem())))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(false, "Could not add item: hibernate error");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void addStoryboardItem_invalidInput() throws Exception {
        mockMvc.perform(post("/greenhouse/storyboard/notnumber/item/add")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new StoryboardItem())))
                .andExpect(status().isBadRequest());
    }

    // removeStoryboardItem(itemId)

    @Test
    void removeStoryboardItem_validInput() throws Exception {
        mockMvc.perform(get("/greenhouse/storyboard/item/1/remove"))
                .andExpect(status().isOk());
    }

    @Test
    void removeStoryboardItem_validInput_mapsToBusinessLogic() throws Exception {
        long itemId = 1234;
        mockMvc.perform(get("/greenhouse/storyboard/item/{itemId}/remove", itemId))
                .andExpect(status().isOk());
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(storyboardItemService, times(1)).removeStoryboardItemById(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(itemId);
    }

    @Test
    void removeStoryboardItem_validInput_returnsApiResponse() throws Exception {
        long itemId = 1234;
        MvcResult mvcResult = mockMvc.perform(get("/greenhouse/storyboard/item/{itemId}/remove", itemId))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(true, "Item removed");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void removeStoryboardItem_notFound_returnsApiResponse() throws Exception {
        doThrow(new ActorNotFoundException("not found")).when(storyboardItemService).removeStoryboardItemById(any(Long.class));

        long itemId = 1234;
        MvcResult mvcResult = mockMvc.perform(get("/greenhouse/storyboard/item/{itemId}/remove", itemId))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(false, "not found");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void removeStoryboardItem_throwsException_returnsApiResponse() throws Exception {
        doThrow(new HibernateException("hibernate error")).when(storyboardItemService).removeStoryboardItemById(any(Long.class));

        long itemId = 1234;
        MvcResult mvcResult = mockMvc.perform(get("/greenhouse/storyboard/item/{itemId}/remove", itemId))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse expectedResponse = new ApiResponse(false, "Could not remove item: hibernate error");
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualToIgnoringWhitespace(actualResponse);
    }

    @Test
    void removeStoryboardItem_invalidInput() throws Exception {
        mockMvc.perform(get("/greenhouse/storyboard/item/notnumber/remove"))
                .andExpect(status().isBadRequest());
    }
}
