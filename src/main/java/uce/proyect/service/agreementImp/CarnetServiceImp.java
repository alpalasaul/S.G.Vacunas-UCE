package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uce.proyect.exceptions.CarnetException;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Carnet;
import uce.proyect.repositories.CarnetRepository;
import uce.proyect.service.agreement.CarnetService;

import java.util.Collection;

@Service
@AllArgsConstructor
public class CarnetServiceImp implements CarnetService {

    private CarnetRepository carnetRepository;

    @Override
    public Carnet agregarOActualizar(Carnet pojo) {
        return this.carnetRepository.save(pojo);
    }

    @Override
    public Collection<Carnet> listar() throws RuntimeException {
        var list = this.carnetRepository.findAll();
        if (list.isEmpty()) {
            throw new RuntimeException("Sin registros");
        }
        return list;
    }

    @Override
    public Carnet buscarPorId(String identificador) {
        var carnet = this.carnetRepository.findById(identificador);
        if (carnet.isPresent()) {
            return carnet.get();
        }
        throw new NoEncontradorException("No existen registros para : ".concat(identificador));
    }

    @Override
    public JSONObject eliminar(String identificador) {
        var carnet = this.carnetRepository.findById(identificador);
        var jsonObject = new JSONObject();
        if (carnet.isPresent()) {
            this.carnetRepository.delete(carnet.get());
            jsonObject.put("Eliminado_C", "Se ha eliminado el carnet: "
                    .concat(carnet.get().get_id()));
        }
        return jsonObject;
    }

//    Servicio que valida que el cerdo ya tenga las 2 dosis
    @Override
    public Carnet buscarCarnetPorEstudiante(String estudiante) throws NoEncontradorException {
        var carnetOptional = this.carnetRepository.findByEstudiante(estudiante);
        if (carnetOptional.isPresent()) {
            var carnet = carnetOptional.get();
            if (!carnet.isSegundaDosis()) {
                var fechaPrimeraDosis = carnet.getFechaPrimeraDosis();
                var fechaEstimadaSegundaDosis = fechaPrimeraDosis.plusDays(28L);
                throw new CarnetException(
                        "No se ha suministrado la segunda dosis aún.",
                        fechaPrimeraDosis,
                        fechaEstimadaSegundaDosis,
                        carnet.getNombreVacuna());
            }
            return carnet;
        }
        throw new NoEncontradorException("No se ha encontrado ningun carnet para :".concat(estudiante));
    }
}
