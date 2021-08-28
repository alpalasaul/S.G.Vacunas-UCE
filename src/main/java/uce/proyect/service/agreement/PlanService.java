package uce.proyect.service.agreement;

import org.json.JSONObject;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Plan;

import java.time.LocalDate;
import java.util.List;

public interface PlanService extends CoreService<Plan> {
    JSONObject generarNotificacionVacuncacion(Plan plan) throws NoEncontradorException;
    JSONObject obtenerEstudiantesAInocular(String facultadNombre, String fase);
    List<Plan> buscarPorFecha(LocalDate fechaInicio);
    JSONObject establecerPlanes();
    JSONObject porcentajeInoculados(String idPlan);
}
