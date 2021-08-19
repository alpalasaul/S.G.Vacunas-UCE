package uce.proyect.service.agreement;

import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;
import uce.proyect.models.Estudiante;

public interface EstudianteService extends CoreService<Estudiante> {
    @Transactional
    JSONObject agregar(Estudiante pojo);

    String nombres(String identificador);
}
