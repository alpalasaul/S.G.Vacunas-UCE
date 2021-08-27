package uce.proyect.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import uce.proyect.models.Plan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends MongoRepository<Plan, String> {
//    Optional<Plan> findByFacultadAndCarrera(String facultad, String carrera);
//    Optional<Plan> findByCarrera(String carrera);
    Optional<Plan> findByFacultad(String facultad);
//    @Query("{fase: ?0, fechaFin: {$lte: ?1}, completo: ?3}")
    List<Plan> findByFaseAndCompletoAndFechaFinLessThanEqual(String fase, boolean completo, LocalDate fechaFin);
    List<Plan> findByFechaInicio(LocalDate fechaInicio);
}
