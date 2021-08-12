package uce.proyect.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uce.proyect.models.Estudiante;

import java.util.Optional;

public interface EstudianteRepository extends MongoRepository<Estudiante, String> {
    Optional<Estudiante> findByUsuario(String usuario);
}
