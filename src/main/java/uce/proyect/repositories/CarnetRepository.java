package uce.proyect.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uce.proyect.models.Carnet;

public interface CarnetRepository extends MongoRepository<Carnet, String> {
}
