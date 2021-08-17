package uce.proyect.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.User;
import uce.proyect.service.agreement.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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

//    Para actualizar el token en el caso de que el princial haya pasoda su ciclo de vida
    @GetMapping("/actualizarToken")
    public void refeshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("SGVUCE ")) { // Defino como debe de ser el inicio del token
            try {
                var token_actualizado = authorizationHeader.substring("SGVUCE ".length()); // obtengo solo la parte del token
                var algorithm = Algorithm.HMAC256("codigoSecreto".getBytes()); // mediante el codigo secreto decifro el algorimto
                var verifier = JWT.require(algorithm).build();
                var decodeJWT = verifier.verify(token_actualizado); // verifico el token
                var nombreUsuario = decodeJWT.getSubject();
                var user = this.userService.buscarPorId(nombreUsuario); // Como en el token actualizado no envio los roles ni la pass debo buscarlo a la bd

                // Genero otro nuevo token
                var nuevo_token = JWT.create()
                        .withSubject(user.getNombreUsuario())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // El + aumenta min
                        .withIssuer(request.getRequestURI())
                        .withClaim("roles", user.getRoles())
                        .sign(algorithm);

                var tokens = new HashMap<String, String>();
                tokens.put("token_actualizado", nuevo_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                response.setHeader("error", e.getMessage());
                response.setStatus(FORBIDDEN.value());
                var error = new HashMap<String, String>();
                error.put("mensaje", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error); // Envio el error como JSON
            }
        } else {
            throw new RuntimeException("No se ha genereado el token actualizado");
        }
    }
}
