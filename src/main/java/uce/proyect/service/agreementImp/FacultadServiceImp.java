package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Facultad;
import uce.proyect.repositories.FacultadRepository;
import uce.proyect.service.agreement.FacultadService;

import java.util.Collection;

@Service
@AllArgsConstructor
public class FacultadServiceImp implements FacultadService {

    private FacultadRepository facultadRepository;

    @Override
    public Facultad agregarOActualizar(Facultad pojo) {
        return this.facultadRepository.save(pojo);
    }

    @Override
    public Collection<Facultad> listar() throws RuntimeException {
        var list = this.facultadRepository.findAll();
        if (list.isEmpty()) {
            throw new RuntimeException("Sin registros");
        }
        return list;
    }

    @Override
    public Facultad buscarPorId(String identificador) throws RuntimeException{
        var facultad = this.facultadRepository.findById(identificador);
        if (facultad.isPresent()) {
            return facultad.get();
        }
        throw new NoEncontradorException("No existen registros para : ".concat(identificador));
    }

    @Override
    public JSONObject eliminar(String identificador) {
        var facultad = this.facultadRepository.findById(identificador);
        var jsonObject = new JSONObject();
        jsonObject.put("Eliminado - F", "No es estudiante");
        if (facultad.isPresent()) {
            this.facultadRepository.delete(facultad.get());
            jsonObject.put("Eliminado - F", "Se ha eliminado a la: "
                    .concat(facultad.get().getNombre())
                    .concat(" la carrera de: ")
                    .concat(facultad.get().getCarrera()));
        }
        return jsonObject;
    }
}
