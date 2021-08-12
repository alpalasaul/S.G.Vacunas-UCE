package uce.proyect.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.User;
import uce.proyect.service.agreement.UserService;

import java.util.Collection;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user) {
        var nUser = this.userService.agregarOActualizar(user, true);
        return new ResponseEntity<User>(nUser, CREATED);
    }
}
