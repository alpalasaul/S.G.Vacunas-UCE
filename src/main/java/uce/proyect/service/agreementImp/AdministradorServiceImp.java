package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Administrador;
import uce.proyect.repositories.AdministradorRepository;
import uce.proyect.repositories.UserRepository;
import uce.proyect.service.agreement.AdministradorService;

import java.util.Collection;

import static uce.proyect.util.FabricaCredenciales.generarIdentificador;
import static uce.proyect.util.FabricaCredenciales.generarUsuario;

@Service
@AllArgsConstructor
public class AdministradorServiceImp implements AdministradorService {

    private AdministradorRepository administradorRepository;

    private UserRepository userRepository; // Al crear admin o estudiante agregar tambien un usuario

    private PasswordEncoder passwordEncoder;

    @Override
    public JSONObject agregar(Administrador pojo, String role) {
        var jsonObject = new JSONObject();

        var usuario = generarUsuario(pojo.getNombres(), pojo.getApellidos(), role);
        jsonObject.put("contrasenaSinEncriptar", usuario.getContrasena());
        pojo.setUsuario(usuario.getNombreUsuario());

        pojo.setIdentificadorAdmin(generarIdentificador());

        usuario.setContrasena(this.passwordEncoder.encode(usuario.getContrasena()));

        var user = userRepository.save(usuario);

        jsonObject.put("usuario", user);

        jsonObject.put("nombreUsuario", user.getNombreUsuario());

        var administrador = this.administradorRepository.save(pojo);

        jsonObject.put("administrador", administrador);

        return jsonObject;
    }

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
        var administrador = this.administradorRepository.findByUsuario(identificador);
        var jsonObject = new JSONObject();
        jsonObject.put("Eliminado_A", "No es administrador");
        if (administrador.isPresent()) {
            this.administradorRepository.delete(administrador.get());
            jsonObject.put("Eliminado_A", "Se ha eliminado a : "
                    .concat(administrador.get().getNombres())
                    .concat(" ")
                    .concat(administrador.get().getApellidos()));
        }
        return jsonObject;
    }
}
