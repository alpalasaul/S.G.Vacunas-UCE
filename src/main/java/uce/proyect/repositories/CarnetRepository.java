package uce.proyect.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uce.proyect.models.Carnet;

import java.util.Optional;

public interface CarnetRepository extends MongoRepository<Carnet, String> {
    Optional<Carnet> findByEstudiante(String estudiante);
}
