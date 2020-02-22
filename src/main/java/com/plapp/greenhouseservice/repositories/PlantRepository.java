package com.plapp.greenhouseservice.repositories;

import com.plapp.entities.greenhouse.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantRepository extends JpaRepository<Plant, Integer> {

}
