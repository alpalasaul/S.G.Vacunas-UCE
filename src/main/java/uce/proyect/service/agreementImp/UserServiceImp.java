package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.User;
import uce.proyect.repositories.UserRepository;
import uce.proyect.service.agreement.AdministradorService;
import uce.proyect.service.agreement.EstudianteService;
import uce.proyect.service.agreement.UserService;

import java.util.Collection;

@Service
@AllArgsConstructor
// En la nueva version de spring, el ID por constructor no necesita @Autowired, lo detecta automaticamente
public class UserServiceImp implements UserService {

    private UserRepository userRepository;

    private AdministradorService administradorService;

    private EstudianteService estudianteService;

    private PasswordEncoder passwordEncoder;

    @Override
    public User agregarOActualizar(User pojo) {
        pojo.setContrasena(this.passwordEncoder.encode(pojo.getContrasena()));
        return this.userRepository.save(pojo);
    }

    @Override
    public Collection<User> listar() throws RuntimeException {
        var list = this.userRepository.findAll();
        if (list.isEmpty()) {
            throw new RuntimeException("Sin registros");
        }
        return list;
    }

    @Override
    public User buscarPorId(String identificador) throws RuntimeException {
        var user = this.userRepository.findByNombreUsuario(identificador);

        if (user.isPresent()) {
            return user.get();
        }
        throw new NoEncontradorException("No existen registros para : ".concat(identificador));
    }

    @Override
    public JSONObject eliminar(String identificador) {
        var user = this.buscarPorId(identificador);

        // Eliminar los demas registros asociados
        var object1 = this.administradorService.eliminar(identificador);
        var object2 = this.estudianteService.eliminar(identificador);

        this.userRepository.delete(user);
        var jsonObject = new JSONObject();
        jsonObject.put("mensaje", "Eliminacion completada");
        jsonObject.put("Administrador", object1.get("Eliminado_A"));
        jsonObject.put("Estudiante", object2.get("Eliminado_E"));
        return jsonObject;
    }
}
