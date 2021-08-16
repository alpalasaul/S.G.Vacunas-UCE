package uce.proyect.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// Clase filtro que se usa para validar la autenticacion del usuario que ingresa por sgv/login y metodo POST
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // El primer paso antes de generar el token es la validacion del usuario a partir de este filtro, mediante sus credenciales
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException { // Tomo los parametros de la peticion http /sgv/login pues es filtrado
        if (!request.getMethod().equals("POST")) { // no captura otros tipos de peticiones
            throw new AuthenticationServiceException("Metodo de autenticación no soportado: " + request.getMethod());
        } else {
            String username = this.obtainUsername(request);
            username = username != null ? username : "";
            username = username.trim();
            String password = this.obtainPassword(request);
            password = password != null ? password : "";
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password); // Lo establesto en un token de autenticacion distinto a JWT
            this.setDetails(request, authRequest);
            return this.authenticationManager.authenticate(authRequest);
        }
    }

    // Defino el token a enviar mediante la libreria de auth0, cuando la autenticacion haya sido correcta
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        var principal = (User) authentication.getPrincipal(); // El principal es el usuario que paso la autenticacion, el user es de la clase userDetails
        var algorithm = Algorithm.HMAC256("codigoSecreto".getBytes()); // defino el tipo de criptografia del algoritmo
        var access_token = JWT.create()
                .withSubject(principal.getUsername()) // Se define un atributo que identifique al usuario, puede ser cualquier otro que se unico
                .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000)) // Defino el tiempo de vida del token 10 min
                .withIssuer(request.getRequestURI().toString())
                .withClaim("roles", principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        // Refresh token
        var refresh_token = JWT.create()
                .withSubject(principal.getUsername()) // Se define un atributo que identifique al usuario, puede ser cualquier otro que se unico
                .withExpiresAt(new Date(System.currentTimeMillis() + 8 * 60 * 1000)) // Defino el tiempo de vida del token, 30 min
                .withIssuer(request.getRequestURI().toString())
                .sign(algorithm);
        var tokens = new HashMap<String, String>();
        tokens.put("token_acceso", access_token);
        tokens.put("token_actualizado", refresh_token);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
