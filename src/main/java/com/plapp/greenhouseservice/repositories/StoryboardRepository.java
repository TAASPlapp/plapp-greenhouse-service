package com.plapp.greenhouseservice.repositories;

import com.plapp.entities.greenhouse.Plant;
import com.plapp.entities.greenhouse.Storyboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoryboardRepository extends JpaRepository<Storyboard, Integer> {
    Storyboard findByPlant(Plant plant);

    Optional<Storyboard> findById(Long id);
    boolean existsById(Long id);
    void deleteById(Long id);
}
