package uce.proyect.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.User;
import uce.proyect.service.agreement.UserService;

import java.util.Collection;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/usuario")
@AllArgsConstructor
public class usuarioController {

    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HC')")
    public ResponseEntity<?> getUsers() {
        var listar = this.userService.listar();
        return new ResponseEntity<Collection<User>>(listar, OK);
    }

    @GetMapping("/{nombreUsuario}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HC')")
    public ResponseEntity<?> getUserbyUserName(@PathVariable("nombreUsuario") String user) {
        var listar = this.userService.buscarPorId(user);
        return new ResponseEntity<User>(listar, OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HC')")
    public ResponseEntity<?> create(@RequestBody User user) {
        var nUser = this.userService.agregarOActualizar(user);
        return new ResponseEntity<User>(nUser, CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HC')")
    public ResponseEntity<?> update(@RequestBody User user) {
        var nUser = this.userService.agregarOActualizar(user);
        return new ResponseEntity<User>(nUser, ACCEPTED);
    }

    @DeleteMapping("/{nombreUsuario}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> delete(@PathVariable("nombreUsuario") String user) {
        var nUser = this.userService.eliminar(user);
        return new ResponseEntity<String>(nUser, ACCEPTED);
    }
}
