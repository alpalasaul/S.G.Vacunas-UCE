package uce.proyect.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.User;
import uce.proyect.service.agreement.UserService;

import java.util.Collection;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

// Completado
@RestController
@RequestMapping("/usuario")
@AllArgsConstructor
public class usuarioController {

    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_HC')") // Significa que tiene permisos el admin y el hc
    public ResponseEntity<?> getUsers() {
        var listar = this.userService.listar();
        return new ResponseEntity<Collection>(listar, OK);
    }

    @GetMapping("/{nombreUsuario}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getUserbyUserName(@PathVariable("nombreUsuario") String user) {
        var listar = this.userService.buscarPorId(user);
        return new ResponseEntity<User>(listar, OK);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')") // Tiene autorizacion el admin, hc y el usuario
    public ResponseEntity<?> update(@RequestBody User user) { // Validar que solo actualize la contrasena, no hay necesidad de actualizar el username
        var nUser = this.userService.agregarOActualizar(user);
        return new ResponseEntity<User>(nUser, ACCEPTED);
    }

    @DeleteMapping("/{nombreUsuario}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map> delete(@PathVariable("nombreUsuario") String user) {
        var mensaje = this.userService.eliminar(user);
        return new ResponseEntity<Map>(mensaje.toMap(), ACCEPTED);
    }
}
