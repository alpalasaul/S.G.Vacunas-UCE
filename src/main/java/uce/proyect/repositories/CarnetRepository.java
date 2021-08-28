package uce.proyect.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uce.proyect.models.Carnet;

import java.util.List;
import java.util.Optional;

public interface CarnetRepository extends MongoRepository<Carnet, String> {
    Optional<Carnet> findByEstudiante(String estudiante);
    Optional<Carnet> findByEstudianteAndInoculacionVoluntaria(String estudiante, boolean inoculacion);
    Optional<Carnet> findByEstudianteAndInoculacionVoluntariaAndPrimeraDosis(String estudiante, boolean inoculacion, boolean primeraDosis);
    Integer findByCentroVacunacionAndInoculacionVoluntaria(String centroVacunacion, boolean inoculacionVoluntaria);
}
