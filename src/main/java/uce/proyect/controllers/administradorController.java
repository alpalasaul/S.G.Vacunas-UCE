package uce.proyect.controllers;


import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.Administrador;
import uce.proyect.service.agreement.AdministradorService;
import uce.proyect.service.agreement.EmailService;

import javax.mail.MessagingException;
import java.io.IOException;

import static org.springframework.http.HttpStatus.*;
import static uce.proyect.util.FabricaCredenciales.ADMIN;
import static uce.proyect.util.FabricaCredenciales.HC;

@RestController
@RequestMapping("/administrador")
@AllArgsConstructor
public class administradorController {

    private EmailService emailService;

    private AdministradorService administradorService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAdministradores() {
        var listar = this.administradorService.listar();
        return new ResponseEntity<>(listar, OK);
    }

    @GetMapping("/{nombreUsuario}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAdministradorbyUserName(@PathVariable("nombreUsuario") String user) {
        var listar = this.administradorService.buscarPorId(user);
        return new ResponseEntity<>(listar, OK);
    }

    @PostMapping("/crearAdmin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createA(@RequestBody Administrador user) throws MessagingException, TemplateException, IOException {
        var nUser = this.administradorService.agregar(user, ADMIN);
        this.emailService.enviarEmailCredenciales(
                user.getCorreo(),
                nUser.get("nombreUsuario").toString(),
                nUser.get("contrasenaSinEncriptar").toString()
        );
        return new ResponseEntity<>(nUser.toMap(), CREATED);
    }

    @PostMapping("/crearControlador")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createH(@RequestBody Administrador user) throws MessagingException, TemplateException, IOException {
        var nUser = this.administradorService.agregar(user, HC);
        this.emailService.enviarEmailCredenciales(
                user.getCorreo(),
                nUser.get("nombreUsuario").toString(),
                nUser.get("contrasenaSinEncriptar").toString()
        );
        return new ResponseEntity<>(nUser.toMap(), CREATED);
    }


    @PutMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> update(@RequestBody Administrador user) {
        var nUser = this.administradorService.agregarOActualizar(user); // No esta manejada la encriptacion de pass
        return new ResponseEntity<>(nUser, ACCEPTED);
    }
}
