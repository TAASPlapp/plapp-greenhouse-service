package com.plapp.greenhouseservice.repositories;

import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryboardRepository extends JpaRepository<Storyboard, Integer> {
    Storyboard findByPlant(Plant plant);

    boolean existsById(Long id);
    void deleteById(Long id);
}
