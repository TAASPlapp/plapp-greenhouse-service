package com.plapp.greenhouseservice.repositories;

import com.plapp.entities.greenhouse.Plant;
import com.plapp.greenhouseservice.entities.StoryboardDPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoryboardRepository extends JpaRepository<StoryboardDPO, Integer> {
    StoryboardDPO findByPlant(Plant plant);
    Optional<StoryboardDPO> findById(Long id);
    List<StoryboardDPO> findAllByPlantOwner(Long owner);

    boolean existsById(Long id);
    void deleteById(Long id);
}
