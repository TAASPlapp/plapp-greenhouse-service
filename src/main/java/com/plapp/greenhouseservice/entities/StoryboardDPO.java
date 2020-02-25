package com.plapp.greenhouseservice.entities;

import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.StoryboardItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class StoryboardDPO {

    @Id
    @GeneratedValue
    private long id;

    private String summary;

    @OneToOne(cascade = {CascadeType.MERGE})
    private Plant plant;

    private Date lastModified;

    @OneToMany(mappedBy = "storyboard",
               cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE},
               orphanRemoval = true)
    private List<StoryboardItemDPO> storyboardItems;
}