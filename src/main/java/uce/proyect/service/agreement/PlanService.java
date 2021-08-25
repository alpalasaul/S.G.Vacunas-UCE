package uce.proyect.service.agreement;

import org.json.JSONObject;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Plan;

public interface PlanService extends CoreService<Plan> {
    JSONObject generarNotificacionVacuncacionPorCarrera(Plan plan) throws NoEncontradorException;
    JSONObject generarNotificacionVacuncacionPorFacultad(Plan plan) throws NoEncontradorException;
}
