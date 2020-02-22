package com.plapp.greenhouseservice.repositories;

import com.plapp.entities.greenhouse.StoryboardItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryboardItemRepository extends JpaRepository<StoryboardItem, Integer> {

}
