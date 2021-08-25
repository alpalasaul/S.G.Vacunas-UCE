package uce.proyect.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uce.proyect.models.Facultad;

import java.util.Optional;

public interface FacultadRepository extends MongoRepository<Facultad, String> {
    Optional<Facultad> findByNombre(String nombre);
}
