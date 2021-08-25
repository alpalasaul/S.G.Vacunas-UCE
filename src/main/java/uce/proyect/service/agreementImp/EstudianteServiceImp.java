package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Estudiante;
import uce.proyect.repositories.CarnetRepository;
import uce.proyect.repositories.EstudianteRepository;
import uce.proyect.repositories.UserRepository;
import uce.proyect.service.agreement.EstudianteService;

import java.util.Collection;
import java.util.stream.Collectors;

import static uce.proyect.util.FabricaCredenciales.*;

@Service
@AllArgsConstructor
@Slf4j
public class EstudianteServiceImp implements EstudianteService {

    private EstudianteRepository estudianteRespository;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private CarnetRepository carnetRepository;

    @Override
    public JSONObject agregar(Estudiante pojo) {
        var jsonObject = new JSONObject();

        var usuario = generarUsuario(pojo.getNombres(), pojo.getApellidos(), EST);
        jsonObject.put("contrasenaSinEncriptar", usuario.getContrasena());
        pojo.setUsuario(usuario.getNombreUsuario());

        usuario.setContrasena(this.passwordEncoder.encode(usuario.getContrasena()));

        var user = userRepository.save(usuario);

        jsonObject.put("usuario", user);
        jsonObject.put("nombreUsuario", user.getNombreUsuario());

        var estudiante = this.estudianteRespository.save(pojo);
        log.info("Agregado estudiante");
//        Cuando se guarde el estudiante se genera un carnet relacionado a dicho usuario
        var carnet = generarCarnet(usuario.getNombreUsuario());

        this.carnetRepository.save(carnet);

        jsonObject.put("estudiante", estudiante);

        jsonObject.put("carnet", "Nuevo Carnet Generado");

        return jsonObject;
    }

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
    public JSONObject eliminar(String identificador) {
        var usuario = this.estudianteRespository.findByUsuario(identificador);
        var jsonObject = new JSONObject();
        jsonObject.put("Eliminado_E", "No es estudiante");
        if (usuario.isPresent()) {
            this.estudianteRespository.delete(usuario.get());
            jsonObject.put("Eliminado_E", "Se ha eliminado a : "
                    .concat(usuario.get().getNombres())
                    .concat(" ")
                    .concat(usuario.get().getApellidos()));
        }
        return jsonObject;
    }

    @Override
    @Transactional(readOnly = true)
    public String nombres(String identificador) {
        var estudiante = this.estudianteRespository.findByUsuario(identificador);
        if (estudiante.isPresent()) {
            return estudiante.get().getNombres().concat(" ").concat(estudiante.get().getApellidos());
        }
        throw new NoEncontradorException("No existen registros para : ".concat(identificador));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Estudiante> buscarEstudiantesPorFacultadYCarrera(String carrera) throws NoEncontradorException {
        var list = this.estudianteRespository.findByCarrera(carrera); // Se busca por carrera pues ya pertenece a una facultad
        if (list.isEmpty()) {
            throw new NoEncontradorException("No existen estudiantes para esta facultad y carrera");
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Estudiante> buscarEstudiantesPorFacultadYCarreraYSemestre(String facultadId, int semestre) throws NoEncontradorException {
        var list = this.estudianteRespository.findByCarrera(facultadId);
        if (list.isEmpty()) {
            throw new NoEncontradorException("No existen estudiantes para esta facultad y carrera");
        }
        var estudiantes = list.stream().filter(estudiante -> estudiante.getSemestre() == semestre).collect(Collectors.toList());
        if (estudiantes.isEmpty()) {
            throw new NoEncontradorException("No existen estudiantes para el semestre especificado");
        }
        return estudiantes;
    }
}
