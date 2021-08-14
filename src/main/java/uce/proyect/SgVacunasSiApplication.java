package uce.proyect;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uce.proyect.models.Administrador;
import uce.proyect.models.Estudiante;
import uce.proyect.models.User;
import uce.proyect.repositories.EstudianteRepository;
import uce.proyect.repositories.UserRepository;
import uce.proyect.service.agreement.AdministradorService;
import uce.proyect.service.agreement.EstudianteService;
import uce.proyect.service.agreement.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

@SpringBootApplication
@Slf4j
@AllArgsConstructor
public class SgVacunasSiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SgVacunasSiApplication.class, args);
    }

//    private UserService userServicel;
//    private UserRepository userRepository;
//    private EstudianteService estudianteRepository;
    private AdministradorService administradorService;

    @Override
    public void run(String... args) throws Exception {
//        var user = new User();
//        user.setNombreUsuario("saul");
//        user.setContrasena("1213");
//        user.setRoles(Arrays.asList("ROLE_HC"));
//        userServicel.agregarOActualizar(user);
//        var user = userRepository.findById("6115c138315eb258e1c3c4b2");
//        log.info(user.get().getNombreUsuario());

//        var estudiante = new Estudiante();
//        estudiante.setNombres("ERICK ENRIQUE");
//        estudiante.setApellidos("DIAZ PASTAZ");
//        estudiante.setCorreo("ediaz@uce.mail");
//        estudiante.setSemestre(6);
//        estudiante.setFacultad("FICFM"); // Se necesita el id del documento
//        estudiante.setFechaNacimiento(LocalDate.now());
//        estudiante.setCedula("111111111");
//        estudiante.setEsControlado(false);
//        estudiante.setGenero("M");
//        estudiante.setTelefono("222222222");
//        this.estudianteRepository.agregar(pojo);


//        this.estudianteRepository.agregarOActualizar(estudiante);

//        var administrador = new Administrador();
//        administrador.setNombres("SAUL");
//        administrador.setApellidos("ALPALA");
//        administrador.setCorreo("salpala@mail");
//        administrador.setIdentificadorAdmin("KDJF22-2");
//        this.administradorService.agregarOActualizar(administrador);
    }
}
