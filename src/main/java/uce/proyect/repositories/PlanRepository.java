package uce.proyect.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uce.proyect.models.Plan;

import java.util.Optional;

public interface PlanRepository extends MongoRepository<Plan, String> {
    Optional<Plan> findByFacultadAndCarrera(String facultad, String carrera);
    Optional<Plan> findByFacultad(String facultad);
}
