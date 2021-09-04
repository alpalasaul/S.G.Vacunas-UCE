package uce.proyect.util;

import uce.proyect.exceptions.PlanException;
import uce.proyect.models.Plan;

import java.util.List;

public class ValidarFechas {

    public static void validarFechas(Plan pojo, List<Plan> lista) throws PlanException {
        if (lista.size() >= 4) { // defino que pueda existir dos facultades máximo
            throw new IllegalArgumentException("No puede existir más de 2 Facultades dentro de la misma fecha");
        }

        if (pojo.getFechaInicio().isAfter(pojo.getFechaFin())) {
            throw new IllegalArgumentException("La fecha inicial no puede ser inferior a la fecha final");
        }

        if (pojo.getFechaInicio().plusDays(2).isBefore(pojo.getFechaFin())) {
            throw new IllegalArgumentException("La distancia entre fechas no debe ser mayor a 2");
        }
    }

    public static void validarFechasActualizacion(Plan pojo, List<Plan> lista) {
        short n_planes = 0;
        // En el caso de que solo actualice el centro de vacunacion no permite actualizar debido a que ya hay mas de 4 fechas
        for (Plan planAntiguo :
                lista) {
            if (!planAntiguo.get_id().equalsIgnoreCase(pojo.get_id())) { // Que no le cuente el plan que tiene el mismo id porque puede tener otro centro de v.
                n_planes++;
            }
        }
        if (n_planes > 3) {
            throw new IllegalArgumentException("No puede existir más de 2 Facultades dentro de la misma fecha");
        } else {
            if (pojo.getFechaInicio().isAfter(pojo.getFechaFin())) {
                throw new IllegalArgumentException("La fecha inicial no puede ser inferior a la fecha final");
            }

            if (pojo.getFechaInicio().plusDays(2).isBefore(pojo.getFechaFin())) {
                throw new IllegalArgumentException("La distancia entre fechas no debe ser mayor a 2");
            }
        }
    }

}
