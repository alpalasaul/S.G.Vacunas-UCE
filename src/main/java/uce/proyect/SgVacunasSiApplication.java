package uce.proyect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uce.proyect.models.User;
import uce.proyect.repositories.UserRepository;
import uce.proyect.service.agreement.UserService;

import java.util.Arrays;
import java.util.Optional;

@SpringBootApplication
@Slf4j
public class SgVacunasSiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SgVacunasSiApplication.class, args);
    }

    @Autowired
    private UserService userServicel;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
//        var user = new User();
//        user.setNombreUsuario("saul");
//        user.setContrasena("1213");
//        user.setRoles(Arrays.asList("ROLE_HC"));
//        userServicel.agregarOActualizar(user);
        var user = userRepository.findById("6115c138315eb258e1c3c4b2");
        log.info(user.get().getNombreUsuario());
    }
}
