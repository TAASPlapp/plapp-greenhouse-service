package com.plapp.greenhouseservice.repositories;

import com.plapp.entities.greenhouse.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlantRepository extends JpaRepository<Plant, Integer> {
    List<Plant> findByOwner(long owner);

    boolean existsById(long id);
    Optional<Plant> findById(long id);
}
