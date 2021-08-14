package uce.proyect.service.agreement;

import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;
import uce.proyect.models.Administrador;

public interface AdministradorService extends CoreService<Administrador> {
    @Transactional
    JSONObject agregar(Administrador pojo, String param); // Este segundo parametro no es necesario para los demas servicios
}
