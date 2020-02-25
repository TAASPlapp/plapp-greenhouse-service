package com.plapp.greenhouseservice.repositories;

import com.plapp.greenhouseservice.entities.StoryboardItemDPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoryboardItemRepository extends JpaRepository<StoryboardItemDPO, Integer> {
    Optional<StoryboardItemDPO> findById(long id);
}
