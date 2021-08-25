package uce.proyect.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uce.proyect.models.Estudiante;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends MongoRepository<Estudiante, String> { // No puedo definir un tipo Persona por que no se guarda en la coleccion Estudiante
    Optional<Estudiante> findByUsuario(String usuario);
    List<Estudiante> findByCarrera(String carrera);
}
