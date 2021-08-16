package uce.proyect.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uce.proyect.models.Plan;

public interface PlanRepository extends MongoRepository<Plan, String> {
}
