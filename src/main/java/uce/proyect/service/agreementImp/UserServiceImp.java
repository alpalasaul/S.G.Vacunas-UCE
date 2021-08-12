package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uce.proyect.models.User;
import uce.proyect.repositories.UserRepository;
import uce.proyect.service.agreement.UserService;

import java.util.Collection;

@Service
@AllArgsConstructor
// En la nueva version de spring, el ID por constructor no necesita @Autowired, lo detecta automaticamente
public class UserServiceImp implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Override
    public User agregarOActualizar(User pojo, boolean flag) throws RuntimeException {
        if (flag) {
            pojo.setContrasena(this.passwordEncoder.encode(pojo.getContrasena()));
            return this.userRepository.insert(pojo);
        }
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
    public User buscarPorId(String identificador) throws Exception {
        var user = this.userRepository.findByNombreUsuario(identificador);
        if (user.isPresent()) {
            return user.get();
        }
        return null; // Manejar la excepcion
    }

    @Override
    public String eliminar(String identificador) throws Exception {
        var user = this.userRepository.findByNombreUsuario(identificador);
        if (user.isPresent()) {
            this.userRepository.delete(user.get());
            return "Eliminacion completada";
        }
        return null; // Manejar la excepcion
    }
}
