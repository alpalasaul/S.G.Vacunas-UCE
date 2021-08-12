package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Estudiante;
import uce.proyect.repositories.EstudianteRepository;
import uce.proyect.service.agreement.EstudianteService;

import java.util.Collection;

@Service
@AllArgsConstructor
public class EstudianteServiceImp implements EstudianteService {

    private EstudianteRepository estudianteRespository;

    @Override
    public Estudiante agregarOActualizar(Estudiante pojo) {
        return this.estudianteRespository.save(pojo);
    }

    @Override
    public Collection<Estudiante> listar() throws RuntimeException {
        var list = this.estudianteRespository.findAll();
        if (list.isEmpty()) {
            throw new RuntimeException("Sin registros");
        }
        return list;
    }

    @Override
    public Estudiante buscarPorId(String identificador) throws RuntimeException {
        var persona = this.estudianteRespository.findByUsuario(identificador);
        if (persona.isPresent()) {
            return persona.get();
        }
        throw new NoEncontradorException("No existen registros para : ".concat(identificador));
    }

    @Override
    public String eliminar(String identificador) {
        var persona = this.buscarPorId(identificador);
        this.estudianteRespository.delete(persona);
        var jsonObject = new JSONObject();
        jsonObject.put("Eliminado", "Se ha eliminado a : "
                .concat(persona.getNombres())
                .concat(" ")
                .concat(persona.getApellidos()));
        return jsonObject.toString();
    }
}
