package uce.proyect.util;

import uce.proyect.exceptions.PlanException;
import uce.proyect.models.Plan;

import java.util.List;

public class ValidarFechas {

    public static void validarFechas(Plan pojo, List<Plan> lista ) {
        if (lista.size() >= 2) { // defino que pueda existir dos facultades máximo
            throw new PlanException("No puede existir más de 2 Facultades dentro de la misma fecha");
        }

        if (pojo.getFechaInicio().isAfter(pojo.getFechaFin())) {
            throw new PlanException("La fecha inicial no puede ser inferior a la fecha final");
        }

        if (pojo.getFechaInicio().plusDays(5).isBefore(pojo.getFechaFin())) {
            throw new PlanException("La distancia entre fechas no debe ser mayor a 5");
        }
    }
}
