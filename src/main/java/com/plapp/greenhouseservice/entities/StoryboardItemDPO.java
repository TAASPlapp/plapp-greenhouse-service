package com.plapp.greenhouseservice.entities;

import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class StoryboardItemDPO {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name="storyboard_id")
    private StoryboardDPO storyboard;

    private String image;
    private String thumbImage;
    private String description;
    private String title;
    private Plant.PlantHealthStatus status;
}
