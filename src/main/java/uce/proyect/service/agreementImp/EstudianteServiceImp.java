package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Estudiante;
import uce.proyect.repositories.EstudianteRepository;
import uce.proyect.repositories.UserRepository;
import uce.proyect.service.agreement.EstudianteService;

import java.util.Collection;

import static uce.proyect.util.FabricaCredenciales.EST;
import static uce.proyect.util.FabricaCredenciales.generarUsuario;

@Service
@AllArgsConstructor
public class EstudianteServiceImp implements EstudianteService {

    private EstudianteRepository estudianteRespository;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Override
    public JSONObject agregar(Estudiante pojo) {
        var jsonObject = new JSONObject();

        var usuario = generarUsuario(pojo.getNombres(), pojo.getApellidos(), EST);
        jsonObject.put("contrasenaSinEncriptar", usuario.getContrasena());
        pojo.setUsuario(usuario.getNombreUsuario());

        usuario.setContrasena(this.passwordEncoder.encode(usuario.getContrasena()));

        var user = userRepository.save(usuario);

        jsonObject.put("usuario", user);

        var estudiante = this.estudianteRespository.save(pojo);

        jsonObject.put("estudiante", estudiante);

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
        jsonObject.put("Eliminado - E", "No es estudiante");
        if (usuario.isPresent()) {
            this.estudianteRespository.delete(usuario.get());
            jsonObject.put("Eliminado - E", "Se ha eliminado a : "
                    .concat(usuario.get().getNombres())
                    .concat(" ")
                    .concat(usuario.get().getApellidos()));
        }
        return jsonObject;
    }
}
