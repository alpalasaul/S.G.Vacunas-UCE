package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Plan;
import uce.proyect.repositories.PlanRepository;
import uce.proyect.service.agreement.PlanService;

import java.util.*;

@Service
@AllArgsConstructor
public class PlanServiceImp implements PlanService {

    private PlanRepository planRepository;

    @Override
    public Plan agregarOActualizar(Plan pojo) {
        return this.planRepository.save(pojo);
    }

    @Override
    public Collection<Plan> listar() throws RuntimeException {
        var list = this.planRepository.findAll();
        if (list.isEmpty()) {
            throw new RuntimeException("Sin registros");
        }
        return list;
    }

    @Override
    public Plan buscarPorId(String identificador) {
        var plan = this.planRepository.findById(identificador);
        if (plan.isPresent()) {
            return plan.get();
        }
        throw new NoEncontradorException("No existen registros para : ".concat(identificador));
    }

    @Override
    public JSONObject eliminar(String identificador) {
        var plan = this.planRepository.findById(identificador);
        var jsonObject = new JSONObject();
        if (plan.isPresent()) {
            this.planRepository.delete(plan.get());
            jsonObject.put("Eliminado - P", "Se ha eliminado el plan: "
                    .concat(plan.get().get_id())
                    .concat(" con programación: ")
                    .concat("I: ".concat(plan.get().getFechaInicio().toString()))
                    .concat(" F: ".concat(plan.get().getFechaFin().toString())));
        }
        return jsonObject;
    }
}
