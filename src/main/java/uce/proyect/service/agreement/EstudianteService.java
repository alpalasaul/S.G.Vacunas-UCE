package uce.proyect.service.agreement;

import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Estudiante;

import java.util.Collection;

public interface EstudianteService extends CoreService<Estudiante> {
    @Transactional
    JSONObject agregar(Estudiante pojo);

    String nombres(String identificador);

    Collection<Estudiante> buscarEstudiantesPorFacultadYCarrera(
            String facultadId
    ) throws NoEncontradorException;

    Collection<Estudiante> buscarEstudiantesPorFacultadYCarreraYSemestre(
            String facultadId,
            int semestre) throws NoEncontradorException;
}
