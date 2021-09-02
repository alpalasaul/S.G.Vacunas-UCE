package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Facultad;
import uce.proyect.repositories.FacultadRepository;
import uce.proyect.service.agreement.FacultadService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class FacultadServiceImp implements FacultadService {

    private FacultadRepository facultadRepository;

    @Override
    public Facultad agregarOActualizar(Facultad pojo) {
        if (pojo.getNombre() != null) {
            return this.facultadRepository.save(pojo);
        }
        throw new IllegalArgumentException("No se puede agregar debido a campos faltantes");
    }

    @Override
    public Collection<Facultad> listar() throws NoEncontradorException {
        var list = this.facultadRepository.findAll();
        if (list.isEmpty()) {
            throw new NoEncontradorException("Sin registros");
        }
        return list;
    }

    @Override
    public Facultad buscarPorId(String identificador) throws NoEncontradorException {
        var facultad = this.facultadRepository.findById(identificador);
        if (facultad.isPresent()) {
            return facultad.get();
        }
        throw new NoEncontradorException("No existen registros para : ".concat(identificador));
    }

    @Override
    public JSONObject eliminar(String identificador) throws NoEncontradorException {
        // con el SPLIT se desborda porque se especifica separar en 2 pero si una carrera tiene 2 palabras se va ala shite xd
        // Solución cambiar el tipo de separador ( ) por (-)
        var strings = identificador.split("-"); // 0 es nombre de la facu, 1 el nombre de la carrera
        var facultad = this.facultadRepository.findByNombre(strings[0]);
        var jsonObject = new JSONObject();
        if (facultad.isPresent() && facultad.get().getCarreras() != null) {
            var carreras = facultad.get().getCarreras();
            short index = 0; // Defino el index en donde se encuentra el nombre de la carrera, no funciona solo con remove
            for (String carrera :
                    carreras) {
                if (carrera.equalsIgnoreCase(strings[1])) {
                    break;
                }
                index++;
            }
            if (carreras.size() < index) { // Valida que no se desborde la list
                throw new NoEncontradorException("No se ha encontrado la carrera "
                        .concat(strings[1])
                        .concat(" de ")
                        .concat(facultad.get().getNombre()));
            }
            carreras.remove(index); // Si no se desborda es porque si existe y lo elimino
            if (!carreras.contains(strings[1])) { // Valido que ya no exista en la lsita

                this.facultadRepository.save(facultad.get());

                jsonObject.put("Eliminado_C", "Se ha eliminado a la carrera "
                        .concat(strings[1])
                        .concat(" de ")
                        .concat(facultad.get().getNombre()));
            }
        } else {
            throw new NoEncontradorException("No ha sido posible eliminar la facultad o carrera");
        }
        return jsonObject;
    }

    @Override // Delego la transaccion al otro metodo
    public Facultad agregarCarrera(String facultad, String carrera) throws NoEncontradorException {
        var facultadOptional = this.facultadRepository.findByNombre(facultad);
        if (facultadOptional.isPresent()) {
            if (facultadOptional.get().getCarreras() == null) { // Agrego la nueva carrera a la lista
                facultadOptional.get().setCarreras(List.of(carrera));
            } else {
                if (facultadOptional.get().getCarreras().contains(carrera)) { // Valido que no exista la carrera
                    throw new IllegalArgumentException("La carrera ya existe, intente de nuevo");
                }
                facultadOptional.get().getCarreras().add(carrera);
            }
            return this.agregarOActualizar(facultadOptional.get());
        }
        throw new NoEncontradorException("No se ha encontrado la facultad : ".concat(facultad));
    }
}
