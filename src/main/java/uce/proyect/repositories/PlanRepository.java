package uce.proyect.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uce.proyect.models.Plan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends MongoRepository<Plan, String> {
    List<Plan> findByFacultad(String facultad);
    Optional<Plan> findByFacultadAndFase(String facultad, String fase);
//    @Query("{fase: ?0, fechaFin: {$lte: ?1}, completo: ?3}")
    List<Plan> findByFaseAndCompletoAndFechaInicioLessThanEqual(String fase, boolean completo, LocalDate fechaFin);
    List<Plan> findByFechaInicioAndFase(LocalDate fechaInicio, String fase);
    List<Plan> findByCompleto(boolean completo);
    List<Plan> findByFechaInicio(LocalDate fechaInicio);
}
