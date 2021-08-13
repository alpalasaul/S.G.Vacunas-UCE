package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Administrador;
import uce.proyect.repositories.AdministradorRepository;
import uce.proyect.service.agreement.AdministradorService;

import java.util.Collection;

@Service
@AllArgsConstructor
public class AdministradorServiceImp implements AdministradorService {

    private AdministradorRepository administradorRepository;

    @Override
    public Administrador agregarOActualizar(Administrador pojo) {
        return this.administradorRepository.save(pojo);
    }

    @Override
    public Collection<Administrador> listar() throws RuntimeException {
        var list = this.administradorRepository.findAll();
        if (list.isEmpty()) {
            throw new RuntimeException("Sin registros");
        }
        return list;
    }

    @Override
    public Administrador buscarPorId(String identificador) throws RuntimeException {
        var admin = this.administradorRepository.findByUsuario(identificador);
        if (admin.isPresent()) {
            return admin.get();
        }
        throw new NoEncontradorException("No existen registros para : ".concat(identificador));
    }

    @Override
    public JSONObject eliminar(String identificador) {
        var administrador = this.buscarPorId(identificador);
        this.administradorRepository.delete(administrador);
        var jsonObject = new JSONObject();
        jsonObject.put("Eliminado", "Se ha eliminado a : "
                .concat(administrador.getNombres())
                .concat(" ")
                .concat(administrador.getApellidos()));
        return jsonObject;
    }
}
