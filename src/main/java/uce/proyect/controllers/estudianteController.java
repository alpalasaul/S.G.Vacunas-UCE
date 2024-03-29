package uce.proyect.controllers;

import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.Estudiante;
import uce.proyect.repositories.EstudianteRepository;
import uce.proyect.service.agreement.EmailService;
import uce.proyect.service.agreement.EstudianteService;

import javax.mail.MessagingException;
import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/estudiante")
@AllArgsConstructor
public class estudianteController {

    private EstudianteService estudianteService;

    private EstudianteRepository estudianteRepository;

    private EmailService emailService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getEstudiantes() {
        var listar = this.estudianteService.listar();
        return new ResponseEntity<>(listar, OK);
    }

    @GetMapping("buscar/{cedula}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> verificarExistenciaCedula(@PathVariable("cedula") String cedula) {
        boolean respuesta = false;
        var existe = estudianteRepository.findByCedula(cedula);
        if (existe.isPresent()) {
            respuesta = true;
        }
        return new ResponseEntity<>(respuesta, OK);
    }

    @GetMapping("filtrarfc/{nombreCarrera}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getEstudiantes(@PathVariable("nombreCarrera") String carrera) {
        var listar = this.estudianteService.buscarEstudiantesPorFacultadYCarrera(carrera); // Obtiene a los usuarios por carrera
        return new ResponseEntity<>(listar, OK);
    }

    @GetMapping("filtrarfc/{nombreCarrera}/{semestre}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getEstudiantes
            (@PathVariable("nombreCarrera") String carrera,
             @PathVariable("semestre") int semestre) {
        var listar = this.estudianteService.buscarEstudiantesPorFacultadYCarreraYSemestre(
                carrera,
                semestre
        );
        return new ResponseEntity<>(listar, OK);
    }

    @GetMapping("/{nombreUsuario}")
    @PreAuthorize("hasRole('ROLE_HC') OR hasRole('ROLE_USER')")
    public ResponseEntity<?> getEstudiantebyUserName(@PathVariable("nombreUsuario") String user) {
        var listar = this.estudianteService.buscarPorId(user);
        return new ResponseEntity<Estudiante>(listar, OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> create(@RequestBody Estudiante user) throws MessagingException, TemplateException, IOException {
        var nUser = this.estudianteService.agregar(user);
        this.emailService.enviarEmailCredenciales(
                user.getCorreo(),
                nUser.get("nombreUsuario").toString(),
                nUser.get("contrasenaSinEncriptar").toString()
        );
        return new ResponseEntity<>(nUser.toMap(), CREATED);
    }

    /*
    Si cambia algun parametro de sus campos personales agregar a una pantalla del admin o hc donde verifiquen y confirmen o cancelen los cambios
    hasta entonces el usuario solo podrá ver un campo que diga solicitud enviada, pero siga observando los datos anteriores a la solicitud

    Solucion 1:
     - Agregar a nivel de persona un atributo que especifique datos actualizados o en espera de confirmacion
     - Duplicar los registros y en el caso de hacer transacciones solo tomar por el documento con el atributo de actualizado
     - Si el hc o admin confirman estos cambios, actualizar el atributo de confirmacion, borrar el documento anterior del estudiante y
     - Todos los documentos asociados al estudiante en el caso de que requieran estos nuevos atributos actualizarlos

    1. Nombres y apellidos Generar un nuevo nombre de usuario
    * */
    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> update(@RequestBody Estudiante user) {
        var nUser = this.estudianteService.agregarOActualizar(user);
        return new ResponseEntity<Estudiante>(nUser, ACCEPTED);
    }
}
