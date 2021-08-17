package uce.proyect.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// Esta clase permite valir el token que el usuario envia
@Slf4j
public class CustomerAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/sgv/login") || request.getServletPath().equals("/usuario/actualizarToken")) {
            filterChain.doFilter(request, response);
        } else {
            var authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("SGVUCE ")) { // Defino como debe de ser el inicio del token
                try {
                    var token = authorizationHeader.substring("SGVUCE ".length()); // obtengo solo la parte del token
                    var algorithm = Algorithm.HMAC256("codigoSecreto".getBytes()); // mediante el codigo secreto decifro el algorimto
                    var verifier = JWT.require(algorithm).build();
                    var decodeJWT = verifier.verify(token); // verifico el token
                    var username = decodeJWT.getSubject(); // obtengo el usaurio
                    var roles = decodeJWT.getClaim("roles").asList(String.class); // obtengo los roles
                    var authorities = new ArrayList<SimpleGrantedAuthority>();
                    roles.forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role));
                    });
                    var authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error al ingresar : {}", e.getMessage());
                    response.setHeader("error", e.getMessage());
                    response.setStatus(FORBIDDEN.value());
                    var error = new HashMap<String, String>();
                    error.put("mensaje", e.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error); // Envio el error como JSON
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
